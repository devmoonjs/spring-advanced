package org.example.expert.domain.manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.config.GlobalExceptionHandler;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerService managerService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private AuthUserArgumentResolver resolver;

    @Autowired
    private ManagerController managerController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(managerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(resolver)
                .build();
    }

    @Test
    void 매니저_저장() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "email@emial.com", UserRole.of("user"));
        long todoId = 1L;
        ManagerSaveRequest request = new ManagerSaveRequest(1L);
        UserResponse user = new UserResponse(1L, "email@email.com");
        ManagerSaveResponse response = new ManagerSaveResponse(1L, user);

        given(managerService.saveManager(authUser, todoId, request)).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform
                (post("/todos/{todoId}/managers", todoId, request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer eyTest"));
        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void 일정에_대한_매니저_조회() throws Exception {
        // given
        long todoId = 1L;
        List<ManagerResponse> responseList = new ArrayList<>();

        given(managerService.getManagers(anyLong())).willReturn(responseList);

        // when
        ResultActions resultActions = mockMvc.perform
                (get("/todos/{todoId}/managers", todoId));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void 일정에_대한_매니저_삭제() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "email@emial.com", UserRole.of("user"));
        long todoId = 1L;
        long mangerId = 1L;

        // when
        ResultActions resultActions = mockMvc.perform
                (delete("/todos/{todoId}/managers/{managerId}", todoId, mangerId));

        // then
        resultActions.andExpect(status().isOk());
    }
}