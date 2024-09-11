package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class CommentAdminServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentAdminService commentAdminService;

    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    public void admin_댓글_삭제시_로그출력(CapturedOutput output) {

        //given
        User user = new User("email", "qwer", UserRole.ADMIN);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = new Todo("title", "test", "weather", user);
        Comment comment = new Comment("test", user, todo);
        ReflectionTestUtils.setField(comment, "id", 1L);

        //when
        commentAdminService.deleteComment(1L);

        //then
        assertThat(output.getOut().contains("::: User ID : {}, Access Time : {}, URL : {} :::"));
    }

    @Test
    public void admin_사용자_역할_변경시_로그출력(CapturedOutput output) {

        //given
        User user = new User("email", "qwer", UserRole.ADMIN);
        ReflectionTestUtils.setField(user, "id", 1L);
        UserRoleChangeRequest request = new UserRoleChangeRequest("user");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        //when
        userAdminService.changeUserRole(1L, request);

        //then
        assertThat(output.getOut().contains("::: User ID : {}, Access Time : {}, URL : {} :::"));
    }
}