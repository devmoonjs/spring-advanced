package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Test
    void 일정저장() {

        // given
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);

        TodoSaveRequest todoSaveRequest = new TodoSaveRequest();
        ReflectionTestUtils.setField(todoSaveRequest, "title", "test");
        ReflectionTestUtils.setField(todoSaveRequest, "contents", "test");

        User user = new User("email@email.com", "1234", UserRole.USER);

        Todo todo = new Todo("title", "test", "weathr", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        given(todoRepository.save(any(Todo.class))).willReturn(todo);

        // when
        TodoSaveResponse todoResponse = todoService.saveTodo(authUser, todoSaveRequest);

        // then
        assertNotNull(todoResponse);
    }

    @Test
    void 일정_리스트_가져오기() {
        // given
        int page = 1;
        int size = 10;

        User user = new User("email@email.com", "1234", UserRole.USER);

        Todo todo = new Todo("title", "test", "weather", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        Todo todo2 = new Todo("title", "test", "weather", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        Pageable pageable = PageRequest.of(page - 1, size);

        List<Todo> todoList = Arrays.asList(todo, todo2);
        Page<Todo> todoPage = new PageImpl<>(todoList, pageable, todoList.size());

        given(todoRepository.findAllByOrderByModifiedAtDesc(any(Pageable.class))).willReturn(todoPage);

        // when
        Page<TodoResponse> responses = todoService.getTodos(page, size);

        // then
        assertEquals(2, responses.getTotalElements());

    }

    @Test
    void 일정_하나만_가져오기() {
        // given
        long todoId = 1L;

        User user = new User("email@email.com", "1234", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = new Todo("title", "test", "weather", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.getTodo(todoId);

        // then
        assertNotNull(response);
    }
}