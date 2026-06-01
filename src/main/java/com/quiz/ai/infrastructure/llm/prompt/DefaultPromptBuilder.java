package com.quiz.ai.infrastructure.llm.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.ai.application.dto.correction.QuestionCorrectionResponse;
import com.quiz.ai.application.service.PromptSettingsService;
import com.quiz.ai.domains.question.Answer;
import com.quiz.ai.domains.question.Question;
import com.quiz.ai.domains.question.SubQuestion;
import com.quiz.ai.domains.subject.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultPromptBuilder implements PromptBuilder {
  private final ObjectMapper objectMapper;
  private final PromptSettingsService promptSettingsService;

  private static final String OUTPUT_FORMAT = """
      {
        "corrections": ["List of specific corrections made"],
        "explanation": "Why these corrections were needed",
        "detected_errors": "Specific errors found in the original",
        "improved_question": {
          "id": "original_id",
          "question": "corrected question text",
          "question_type": "question_type",
          "image": "image_url or null",
          "feedback": "corrected feedback",
          "feedback_audio": "audio_url or null",
          "question_audio": "audio_url or null",
          "order_num": "order_number",
          "objective": {
            "id": "objective_id",
            "objective": "objective text"
          },
          "sub_questions": [
            {
              "id": "sub_question_id",
              "question": "corrected sub question",
              "order_num": "order_number",
              "answers": [
                {
                  "id": "answer_id",
                  "answer": "corrected answer",
                  "image": "image_url or null",
                  "is_right": true,
                  "answer_audio": "audio_url or null",
                  "order_num": "order_number"
                }
              ]
            }
          ]
        }
      }
      """;

  @Override
  public String buildSystemMessage(Course course) {
    var settings = promptSettingsService.getPromptSettings();
    Map<String, String> tokens = new LinkedHashMap<>();
    tokens.put("level", valueOrEmpty(course.getLevel() == null ? null : course.getLevel().getLevelName()));
    tokens.put("subject", valueOrEmpty(course.getSubject() == null ? null : course.getSubject().getTitle()));
    tokens.put("domain", valueOrEmpty(course.getDomain() == null ? null : course.getDomain().getTitle()));
    tokens.put("semester", valueOrEmpty(course.getSemester() == null ? null : course.getSemester().name()));
    tokens.put("pedagogical_rules", valueOrEmpty(settings.getPedagogicalRules()));
    return renderTemplate(settings.getSystemMessageTemplate(), tokens);
  }

  @Override
  public String buildQuestionCorrectionPrompt(Question question, Course course) {
    var settings = promptSettingsService.getPromptSettings();
    String questionJson = toJson(buildQuestionPayload(question));

    Map<String, String> tokens = new LinkedHashMap<>();
    tokens.put("question_json", questionJson);
    tokens.put("output_format", OUTPUT_FORMAT);
    return renderTemplate(settings.getCorrectionPromptTemplate(), tokens);
  }

  @Override
  public String buildQuestionCorrectionChatPrompt(
      Question question,
      Course course,
      QuestionCorrectionResponse previousCorrection,
      String userMessage) {
    var settings = promptSettingsService.getPromptSettings();
    String questionJson = toJson(buildQuestionPayload(question));
    String previousCorrectionJson = previousCorrection == null ? "null" : toJson(previousCorrection);
    String instruction = userMessage == null ? "" : userMessage.trim();

    Map<String, String> tokens = new LinkedHashMap<>();
    tokens.put("instruction", instruction);
    tokens.put("previous_correction_json", previousCorrectionJson);
    tokens.put("question_json", questionJson);
    tokens.put("output_format", OUTPUT_FORMAT);
    return renderTemplate(settings.getChatPromptTemplate(), tokens);
  }

  /*--Utils---*/
  private String renderTemplate(String template, Map<String, String> tokens) {
    String resolved = template == null ? "" : template;
    for (Map.Entry<String, String> entry : tokens.entrySet()) {
      String token = "{{" + entry.getKey() + "}}";
      resolved = resolved.replace(token, valueOrEmpty(entry.getValue()));
    }
    return resolved;
  }

  private String valueOrEmpty(String value) {
    return value == null ? "" : value;
  }

  private String toJson(Object object) {
    try {
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (Exception e) {
      log.error("Error serializing object to JSON", e);
      throw new RuntimeException("Failed to serialize object to JSON", e);
    }
  }

  private Map<String, Object> buildQuestionPayload(Question question) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("id", question.getId());
    payload.put("question", question.getQuestion());
    payload.put("question_type", question.getQuestionType() != null ? question.getQuestionType().name() : null);
    payload.put("image", question.getImage());
    payload.put("feedback", question.getFeedback());
    payload.put("feedback_audio", question.getFeedbackAudio());
    payload.put("question_audio", question.getQuestionAudio());
    payload.put("order_num", question.getOrderNum());
    payload.put("objective", question.getObjective() == null ? null : buildObjectivePayload(question));
    payload.put("sub_questions", question.getSubQuestions() == null ? List.of()
        : question.getSubQuestions().stream()
            .map(this::buildSubQuestionPayload)
            .toList());
    return payload;
  }

  private Map<String, Object> buildObjectivePayload(Question question) {
    Map<String, Object> objective = new LinkedHashMap<>();
    objective.put("id", question.getObjective().getId());
    objective.put("objective", question.getObjective().getObjective());
    return objective;
  }

  private Map<String, Object> buildSubQuestionPayload(SubQuestion subQuestion) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("id", subQuestion.getId());
    payload.put("question", subQuestion.getQuestion());
    payload.put("order_num", subQuestion.getOrderNum());
    payload.put("answers", subQuestion.getAnswers() == null ? List.of()
        : subQuestion.getAnswers().stream()
            .map(this::buildAnswerPayload)
            .toList());
    return payload;
  }

  private Map<String, Object> buildAnswerPayload(Answer answer) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("id", answer.getId());
    payload.put("answer", answer.getAnswer());
    payload.put("image", answer.getImage());
    payload.put("is_right", answer.isRight());
    payload.put("answer_audio", answer.getAnswerAudio());
    payload.put("order_num", answer.getOrderNum());
    return payload;
  }
}
