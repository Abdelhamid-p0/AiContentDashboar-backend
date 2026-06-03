package com.quiz.ai.interfaces.rest;

import com.quiz.ai.application.dto.correction.QuestionCorrectionResponse;
import com.quiz.ai.application.dto.question.AnswerResponse;
import com.quiz.ai.application.dto.question.ObjectiveResponse;
import com.quiz.ai.application.dto.question.QuestionResponse;
import com.quiz.ai.application.dto.question.SubQuestionResponse;
import com.quiz.ai.application.service.QuestionCorrectionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuestionCorrectionControllerIntegrationTest {

        private static final String QUESTION_ID = "ae8dc392-b311-4e58-ad2b-265537563210";

        @Test
        void shouldReturnCorrectedQuestionForProvidedQuestionId() throws Exception {
                QuestionCorrectionService questionCorrectionService = mock(QuestionCorrectionService.class);
                QuestionCorrectionController controller = new QuestionCorrectionController(questionCorrectionService);
                MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

                QuestionResponse improvedQuestion = new QuestionResponse(
                                QUESTION_ID,
                                "What is the solution to 2x - 2 = 2?",
                                "ONE_CHOICE",
                                "question_image",
                                "Corrected feedback",
                                null,
                                "question_audio",
                                1,
                                new ObjectiveResponse("objective-1", "Solve linear equations"),
                                List.of(new SubQuestionResponse(
                                                "sub-question-1",
                                                "Solve: 2x - 2 = 2",
                                                1,
                                                List.of(
                                                                new AnswerResponse("answer-1", "x = 2",
                                                                                "answer_image_1", true,
                                                                                "answer_audio_1", 1),
                                                                new AnswerResponse("answer-2", "x = 5",
                                                                                "answer_image_2", false,
                                                                                "answer_audio_2",
                                                                                2)))));

                QuestionCorrectionResponse mockedResponse = new QuestionCorrectionResponse(
                                List.of("Corrected wording and feedback"),
                                "The question was clarified and the answer choices were normalized.",
                                "Ambiguous wording in the original question",
                                improvedQuestion,
                                improvedQuestion);

                when(questionCorrectionService.correctQuestion(QUESTION_ID)).thenReturn(mockedResponse);

                mockMvc.perform(get("/api/v1/questions/{questionId}/correct", QUESTION_ID))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.corrections[0]").value("Corrected wording and feedback"))
                                .andExpect(jsonPath("$.explanation")
                                                .value("The question was clarified and the answer choices were normalized."))
                                .andExpect(jsonPath("$.detected_errors")
                                                .value("Ambiguous wording in the original question"))
                                .andExpect(jsonPath("$.improved_question.id").value(QUESTION_ID))
                                .andExpect(jsonPath("$.improved_question.question")
                                                .value("What is the solution to 2x - 2 = 2?"))
                                .andExpect(jsonPath("$.improved_question.sub_questions[0].answers[0].is_right")
                                                .value(true));
        }
}