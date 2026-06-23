package com.quiz.ai.tools;

import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BackupImporter {

    private BackupImporter() {
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> arguments = parseArguments(args);
        String sqlFileSetting = firstNonBlank(
                firstNonBlank(arguments.get("sql-file"), System.getenv("BACKUP_SQL_FILE")),
                "backup.sql");
        Path sqlFile = Paths.get(sqlFileSetting);

        String jdbcUrl = firstNonBlank(
                System.getenv("NEON_JDBC_URL"),
                buildJdbcUrlFromParts());
        String username = System.getenv("NEON_USERNAME");
        String password = System.getenv("NEON_PASSWORD");

        if (jdbcUrl == null || username == null || password == null) {
            throw new IllegalStateException("Missing Neon connection environment variables.");
        }

        if (!Files.exists(sqlFile)) {
            throw new IllegalStateException("SQL file not found: " + sqlFile.toAbsolutePath());
        }

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            connection.setAutoCommit(false);
            resetPublicSchema(connection);
            importDump(connection, sqlFile);
            connection.commit();
        }
    }

    private static void resetPublicSchema(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA IF EXISTS public CASCADE");
            statement.execute("CREATE SCHEMA public");
            statement.execute("GRANT ALL ON SCHEMA public TO public");
        }
    }

    private static void importDump(Connection connection, Path sqlFile) throws IOException, SQLException {
        try (BufferedReader reader = Files.newBufferedReader(sqlFile, StandardCharsets.UTF_8)) {
            StringBuilder statement = new StringBuilder();
            boolean inBlockComment = false;
            boolean inSingleQuote = false;
            boolean inDoubleQuote = false;

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }

                if (trimmed.matches("(?i)^ALTER\\s+.*\\s+OWNER\\s+TO\\s+.*;?$")) {
                    continue;
                }

                if (trimmed.startsWith("\\") && !trimmed.equals("\\.")) {
                    continue;
                }

                if (trimmed.startsWith("COPY ") && trimmed.endsWith("FROM stdin;")) {
                    flushStatement(connection, statement);

                    String copySql = trimmed.substring(0, trimmed.length() - 1);
                    StringBuilder copyData = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("\\.")) {
                            break;
                        }
                        copyData.append(line).append('\n');
                    }

                    executeCopy(connection, copySql, copyData.toString());
                    continue;
                }

                statement.append(line).append('\n');
                int completedIndex = findStatementEnd(statement, inBlockComment, inSingleQuote, inDoubleQuote);
                if (completedIndex >= 0) {
                    String sql = statement.substring(0, completedIndex).trim();
                    if (!sql.isEmpty()) {
                        executeStatement(connection, sql);
                    }
                    String remainder = statement.substring(completedIndex + 1);
                    statement.setLength(0);
                    statement.append(remainder);
                    inBlockComment = false;
                    inSingleQuote = false;
                    inDoubleQuote = false;
                }
            }

            flushStatement(connection, statement);
        }
    }

    private static void executeStatement(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static void executeCopy(Connection connection, String copySql, String copyData)
            throws SQLException, IOException {
        PGConnection pgConnection = connection.unwrap(PGConnection.class);
        CopyManager copyManager = pgConnection.getCopyAPI();
        try (Reader reader = new StringReader(copyData)) {
            copyManager.copyIn(copySql, reader);
        }
    }

    private static void flushStatement(Connection connection, StringBuilder statement) throws SQLException {
        String sql = statement.toString().trim();
        if (!sql.isEmpty()) {
            executeStatement(connection, sql);
        }
        statement.setLength(0);
    }

    private static int findStatementEnd(StringBuilder builder, boolean inBlockComment, boolean inSingleQuote,
            boolean inDoubleQuote) {
        boolean blockComment = inBlockComment;
        boolean singleQuote = inSingleQuote;
        boolean doubleQuote = inDoubleQuote;

        for (int index = 0; index < builder.length(); index++) {
            char current = builder.charAt(index);

            if (blockComment) {
                if (current == '*' && index + 1 < builder.length() && builder.charAt(index + 1) == '/') {
                    blockComment = false;
                    index++;
                }
                continue;
            }

            if (singleQuote) {
                if (current == '\'') {
                    if (index + 1 < builder.length() && builder.charAt(index + 1) == '\'') {
                        index++;
                    } else {
                        singleQuote = false;
                    }
                }
                continue;
            }

            if (doubleQuote) {
                if (current == '"') {
                    doubleQuote = false;
                }
                continue;
            }

            if (current == '/' && index + 1 < builder.length() && builder.charAt(index + 1) == '*') {
                blockComment = true;
                index++;
                continue;
            }

            if (current == '\'') {
                singleQuote = true;
                continue;
            }

            if (current == '"') {
                doubleQuote = true;
                continue;
            }

            if (current == ';') {
                return index;
            }
        }

        return -1;
    }

    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (String argument : args) {
            if (argument.startsWith("--")) {
                String content = argument.substring(2);
                int equalsIndex = content.indexOf('=');
                if (equalsIndex >= 0) {
                    arguments.put(content.substring(0, equalsIndex), content.substring(equalsIndex + 1));
                } else {
                    arguments.put(content, "true");
                }
            }
        }
        return arguments;
    }

    private static String buildJdbcUrlFromParts() {
        String host = System.getenv("NEON_HOST");
        String database = System.getenv("NEON_DATABASE");
        if (host == null || database == null) {
            return null;
        }

        String port = firstNonBlank(System.getenv("NEON_PORT"), "5432");
        return "jdbc:postgresql://" + host + ":" + port + "/" + database + "?sslmode=require";
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}