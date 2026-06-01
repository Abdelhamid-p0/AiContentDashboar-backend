# LLM Integration Architecture - Question Correction System

## Overview

This system provides automated question correction using Large Language Models (LLM) with a clean, modular, and extensible architecture following SOLID principles.

## Architecture Components

### 1. **LLM Client Layer** (`infrastructure.llm.client`)

- **Interface**: `LLMClient` - Abstract interface for LLM communication
- **Implementation**: `KimiLLMClient` - Concrete implementation using Kimi API

**Why this approach:**

- Easy model switching by implementing new `LLMClient` (e.g., `OpenAILLMClient`, `AnthropicLLMClient`)
- Dependency Injection for testability
- Configuration-driven model selection

### 2. **Configuration Layer** (`infrastructure.llm.config`)

- **LLMProperties**: Configuration class with `@ConfigurationProperties`
- **LLMClientConfiguration**: RestTemplate and connection configuration

**Configurable parameters:**

```properties
quiz-ai.llm.base-url=https://api.moonshot.cn/v1
quiz-ai.llm.api-key=${KIMI_API_KEY}
quiz-ai.llm.model=kimi-k2-0711-preview
quiz-ai.llm.timeout=30s
quiz-ai.llm.max-tokens=2000
quiz-ai.llm.temperature=0.7
```

### 3. **Prompt Engineering Layer** (`infrastructure.llm.prompt`)

- **Interface**: `PromptBuilder` - Abstract prompt construction
- **Implementation**: `DefaultPromptBuilder` - Pedagogical prompt creation

**Key features:**

- Dynamic system messages based on course context (Level, Subject, Domain)
- Pedagogical rules embedded in prompts
- Structured output format specification
- Question context as JSON

### 4. **Service Layer** (`application.service`)

- **Interface**: `QuestionCorrectionService`
- **Implementation**: `DefaultQuestionCorrectionService`

**Responsibilities:**

- Orchestration of correction workflow
- Question retrieval
- LLM invocation
- Error handling

### 5. **Controller Layer** (`interfaces.rest`)

- **QuestionCorrectionController**: REST endpoint
- Endpoint: `GET /api/v1/questions/{questionId}/correct`

## SOLID Principles Applied

### 1. **Single Responsibility Principle**

- `LLMClient`: Only handles LLM API communication
- `PromptBuilder`: Only constructs prompts
- `QuestionCorrectionService`: Only orchestrates the workflow
- `LLMProperties`: Only manages configuration

### 2. **Open/Closed Principle**

- New LLM implementations can be added without modifying existing code
- New prompt strategies can be added via new `PromptBuilder` implementations

### 3. **Liskov Substitution Principle**

- Any `LLMClient` implementation can replace `KimiLLMClient`
- Any `PromptBuilder` implementation can replace `DefaultPromptBuilder`

### 4. **Interface Segregation Principle**

- `LLMClient` interface focused on core chat functionality
- `PromptBuilder` interface focused on prompt construction
- Small, focused interfaces

### 5. **Dependency Inversion Principle**

- High-level modules depend on abstractions (`LLMClient`, `PromptBuilder`)
- Low-level modules implement abstractions
- Dependencies injected via constructors

## Extensibility Points

### For Future Evaluation System

```java
// Interface ready for implementation
public interface CorrectionsEvaluator {
    EvaluationResult evaluate(QuestionCorrectionResponse response);
    boolean isQualityAcceptable(QuestionCorrectionResponse response);
}
```

**Planned implementations:**

- ML-based quality scoring
- Pedagogical rule validation
- Answer correctness verification

### For Future RAG System

```java
// Interface ready for implementation
public interface RAGContext {
    String retrieveSimilarQuestionsContext(Question question, int limit);
    String retrievePedagogicalGuidelines(String subjectId, String domainId);
    String retrieveCommonMistakes(String questionType, String level);
}
```

**Planned implementations:**

- Vector DB integration (Pinecone, Weaviate, Milvus)
- Elasticsearch for semantic search
- Knowledge base queries

**Current**: `StubRAGContext` as placeholder

## Switching LLM Models

### To use a different model:

1. **Option 1: Configuration Only**

```properties
# Switch Kimi model
quiz-ai.llm.model=kimi-k2-full

# Or use different base URL
quiz-ai.llm.base-url=https://api.openai.com/v1
```

2. **Option 2: Different LLM Provider**

```java
@Component
public class OpenAILLMClient implements LLMClient {
    // Implementation for OpenAI API
}
```

3. **Spring will automatically select the correct implementation via:**

- Component scanning
- Primary annotation if multiple implementations exist
- Property-based selection

## Data Flow

```
1. GET /api/v1/questions/{questionId}/correct
   ↓
2. QuestionCorrectionController
   ↓
3. QuestionCorrectionService.correctQuestion()
   ├─ Load Question from Repository
   ├─ Get Course context
   ├─ Call PromptBuilder.buildSystemMessage()
   ├─ Call PromptBuilder.buildQuestionCorrectionPrompt()
   ├─ Call LLMClient.chatWithStructuredResponse()
   │  ├─ KimiLLMClient calls Kimi API
   │  ├─ Sends system message + user message
   │  └─ Parses JSON response
   └─ Return QuestionCorrectionResponse
   ↓
4. Response to Client
```

## Prompt Structure

### System Message (Dynamic)

```
- Role: Expert pedagogical assistant
- Context: Level, Subject, Domain, Semester
- Pedagogical rules
- Instructions for correction
```

### User Message

```
- Question data (JSON)
- Task description
- Output format specification
- Clear instructions
```

### Expected Output

```json
{
  "corrections": "...",
  "explanation": "...",
  "detected_errors": "...",
  "improved_question": {
    /* Full question object */
  }
}
```

## Configuration for Different Environments

### Development (application-dev.properties)

```properties
quiz-ai.llm.model=kimi-k2-0711-preview
quiz-ai.llm.temperature=0.7
quiz-ai.llm.timeout=30s
```

### Production (future - application-prod.properties)

```properties
quiz-ai.llm.model=kimi-k2-full
quiz-ai.llm.temperature=0.5
quiz-ai.llm.timeout=60s
quiz-ai.llm.max-tokens=4000
```

## Testing Strategy

### Unit Tests

- Mock `LLMClient` for service testing
- Test `PromptBuilder` output format
- Validate configuration loading

### Integration Tests

- Test with real Kimi API (requires valid API key)
- Test end-to-end workflow

## Security Considerations

1. **API Key Management**
   - Uses environment variable: `KIMI_API_KEY`
   - Never hardcode credentials
   - Inject via configuration properties

2. **Input Validation**
   - Validate question ID exists
   - Handle missing questions gracefully

3. **Error Handling**
   - Catch API failures
   - Return meaningful error messages
   - Log sensitive information securely

4. **Rate Limiting** (Future)
   - Add request throttling
   - Implement circuit breaker pattern
   - Cache responses when appropriate

## Future Enhancements

1. **RAG Integration**
   - Load similar questions as context
   - Retrieve pedagogical guidelines
   - Implement vector similarity search

2. **Evaluation System**
   - Validate correction quality
   - Score confidence levels
   - Flag low-quality corrections

3. **Caching**
   - Cache frequent corrections
   - Implement cache invalidation

4. **Batch Processing**
   - Correct multiple questions
   - Asynchronous processing
   - Job status tracking

5. **Monitoring**
   - Track correction success rates
   - Monitor API costs
   - Alert on failures

## Migration Path (Kimi → Other Models)

If switching to OpenAI or Claude:

1. Create new implementation class
2. Update `@Bean` or use `@Primary`
3. Update configuration properties
4. Adjust prompt format if needed (mostly compatible)
5. Test with new model
6. Deploy configuration change
