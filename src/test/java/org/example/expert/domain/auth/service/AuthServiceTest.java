package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void 회원가입() {
        // given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "email", "email@email.com");
        ReflectionTestUtils.setField(signupRequest, "password", "1234");
        ReflectionTestUtils.setField(signupRequest, "userRole", "admin");

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        UserRole userRole = UserRole.of(signupRequest.getUserRole());
        User saveUser = new User(signupRequest.getEmail(), encodedPassword, userRole);
        ReflectionTestUtils.setField(saveUser, "id", 1L);
        String testToken = "test-token";

        given(userRepository.save(any(User.class))).willReturn(saveUser);
        given(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class))).willReturn(testToken);

        // when
        SignupResponse response = authService.signup(signupRequest);

        // then
        assertNotNull(response);

    }

    @Test
    void 이메일_유효성_검사() {
        // given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "password", "1234");
        ReflectionTestUtils.setField(signupRequest, "userRole", "admin");
        ReflectionTestUtils.setField(signupRequest, "email", "");

        // when
        AuthException exception = assertThrows(AuthException.class, () -> authService.signup(signupRequest));

        // then
        assertEquals("이메일을 입력하세요.", exception.getMessage());
    }

    @Test
    void 이메일_중복() {
        // given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "password", "1234");
        ReflectionTestUtils.setField(signupRequest, "userRole", "admin");
        ReflectionTestUtils.setField(signupRequest, "email", "email@emial.com");

        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when
        AuthException exception = assertThrows(AuthException.class, () -> authService.signup(signupRequest));

        // then
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    void 로그인() {
        // given
        SigninRequest signinRequest = new SigninRequest();
        ReflectionTestUtils.setField(signinRequest, "email", "email@email.com");
        ReflectionTestUtils.setField(signinRequest, "password", "test");

        User user = new User();
        ReflectionTestUtils.setField(user, "email", signinRequest.getEmail());
        ReflectionTestUtils.setField(user, "password", signinRequest.getPassword());
        ReflectionTestUtils.setField(user, "userRole", UserRole.ADMIN);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        // when
        SigninResponse response = authService.signin(signinRequest);

        // then
        assertNotNull(response);
    }

    @Test
    void 로그인시_가입되지_않은_유저() {
        // given
        SigninRequest signinRequest = new SigninRequest();
        ReflectionTestUtils.setField(signinRequest, "email", "email@eamil.com");

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.signin(signinRequest));

        // then
        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }

    @Test
    void 로그인시_잘못된_비밀번호_오류() {
        // given
        SigninRequest signinRequest = new SigninRequest();
        ReflectionTestUtils.setField(signinRequest, "email", "email@eamil.com");
        ReflectionTestUtils.setField(signinRequest, "password", "1234");

        User user = new User();
        ReflectionTestUtils.setField(user, "email", signinRequest.getEmail());
        ReflectionTestUtils.setField(user, "password", signinRequest.getPassword());
        ReflectionTestUtils.setField(user, "userRole", UserRole.ADMIN);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())).willReturn(false);

        // when
        AuthException exception = assertThrows(AuthException.class, () -> authService.signin(signinRequest));

        // then
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }
}