package org.example.expert.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.config.GlobalExceptionHandler;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentController controller;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private AuthUserArgumentResolver resolver;

    @Autowired
    private CommentController commentController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(resolver)
                .build();
    }

    @Test
    void 댓글저장() throws Exception {
        // given
        long todoId = 1L;
        CommentSaveRequest request = new CommentSaveRequest("test");
        UserResponse user = new UserResponse(1L, "email@email.com");
        CommentSaveResponse response = new CommentSaveResponse(1L, "test", user);
        given(commentService.saveComment(any(), anyLong(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform
                (post("/todos/{todoId}/comments", todoId, request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void 댓글_가져오기() throws Exception {
        // given
        long todoId = 1L;
        List<CommentResponse> responseList = new ArrayList<>();
        given(commentService.getComments(anyLong())).willReturn(responseList);

        // when
        ResultActions resultActions = mockMvc.perform((get("/todos/{todoId}/comments", todoId)));

        // then
        resultActions.andExpect(status().isOk());
    }
}