package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void 비밀번호_변경() {
        // given
        long userId = 1L;

        UserChangePasswordRequest request = new UserChangePasswordRequest(
                "1234", "Test12345"
        );

        String encodedOldPw = passwordEncoder.encode("1234");
        User user = new User("email@email.com", encodedOldPw, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        String encodedPassword = "testPw1234";

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        userService.changePassword(userId, request);

        // then
        verify(passwordEncoder).encode(request.getNewPassword());
    }

    @Test
    void 비밀번호_변경시_기존_비밀번호와_같을경우_InvalidException_발생() {
        // given
        long userId = 1L;

        UserChangePasswordRequest request = new UserChangePasswordRequest(
                "Test12345", "Test12345"
        );

        String encodedOldPw = passwordEncoder.encode("Test12345");
        User user = new User("email@email.com", encodedOldPw, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(userId, request));

        // then
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 비밀번호_변경시_기존비밀번호_틀릴경우_InvalidException_발생() {
        // given
        long userId = 1L;

        UserChangePasswordRequest request = new UserChangePasswordRequest(
                "Abcd12345", "Test111111"
        );

        String encodedOldPw = passwordEncoder.encode("Test12345");
        User user = new User("email@email.com", encodedOldPw, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(userId, request));

        // then
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    void 변경할_비밀번호_유효성_오류시_InvalidException_발생() {
        // given
        long userId = 1L;

        UserChangePasswordRequest request = new UserChangePasswordRequest(
                "Test12345", "123"
        );

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(userId, request));

        // then
        assertEquals("새 비밀번호는 8 자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
    }

    @Test
    void 변경할_비밀번호_숫자없는_오류시_InvalidException_발생() {
        // given
        long userId = 1L;

        UserChangePasswordRequest request = new UserChangePasswordRequest(
                "Test12345", "abcdefghij"
        );

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(userId, request));

        // then
        assertEquals("새 비밀번호는 8 자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
    }

    @Test
    void 변경할_비밀번호_대문자없는_오류시_InvalidException_발생() {
        // given
        long userId = 1L;

        UserChangePasswordRequest request = new UserChangePasswordRequest(
                "Test12345", "abcdefghij12345"
        );

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(userId, request));

        // then
        assertEquals("새 비밀번호는 8 자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
    }

    @Test
    void 유저_가져오기() {
        // given
        long userId = 1L;
        User user = new User("email@email.com", "encodedOldPw", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUser(userId);

        // then
        assertNotNull(response);
    }
}