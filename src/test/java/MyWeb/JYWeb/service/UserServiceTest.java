package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.LoginRequest;
import MyWeb.JYWeb.DTO.RefreshRequest;
import MyWeb.JYWeb.DTO.TokenResponse;
import MyWeb.JYWeb.DTO.RegisterRequest;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.exception.custom.DuplicateLoginIdException;
import MyWeb.JYWeb.exception.custom.UnauthorizedException;
import MyWeb.JYWeb.exception.custom.ValidateLoginException;
import MyWeb.JYWeb.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    public void cleanUp() {
        redisTemplate.delete("testuser");
    }


    @Test
    @DisplayName("회원가입 성공")
    public void register_success() {
        // given
        RegisterRequest form = new RegisterRequest();
        form.setLoginId("newuser");
        form.setPassword("password123");
        form.setNickname("닉네임");

        // when
        userService.registerUser(form);

        // then
        Optional<User> saved = userRepository.findByLoginId("newuser");
        assertTrue(saved.isPresent());
        assertEquals("닉네임", saved.get().getNickname());
    }

    @Test
    @DisplayName("중복된 아이디로 회원가입 시 예외 발생")
    public void register_duplicateId_exception() {
        // given
        User user = new User();
        user.setLoginId("duplicate");
        user.setPassword("pass");
        user.setNickname("중복유저");
        userRepository.save(user);

        RegisterRequest form = new RegisterRequest();
        form.setLoginId("duplicate");
        form.setPassword("newpass");
        form.setNickname("새유저");

        // when & then
        assertThrows(DuplicateLoginIdException.class, () -> userService.registerUser(form));
    }

    @Test
    @DisplayName("로그인 성공")
    public void login_success() {
        //given
        String loginId = "testuser";
        String password = "1234";
        LoginRequest loginRequestDTO = new LoginRequest(loginId, password);

        //when
        TokenResponse tokenResponse = userService.validateUser(loginRequestDTO);

        //then
        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getAccessToken());
        assertNotNull(tokenResponse.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패")
    public void login_fail() {
        //given
        String loginId1 = "testuser";
        String password1 = "1234_fail";
        LoginRequest loginRequestDTO1 = new LoginRequest(loginId1, password1);

        String loginId2 = "test";
        String password2 = "1234_fail";
        LoginRequest loginRequestDTO2 = new LoginRequest(loginId2, password2);

        //when && then
        assertThrows(ValidateLoginException.class, () -> userService.validateUser(loginRequestDTO1));
        assertThrows(ValidateLoginException.class, () -> userService.validateUser(loginRequestDTO2));

    }

    @Test
    @DisplayName("리프레쉬 토큰 재발급 성공")
    public void refreshAccessToken_success(){
        //given
        String loginId = "testuser";
        String password = "1234";
        LoginRequest loginRequestDTO = new LoginRequest(loginId, password);

        TokenResponse tokenResponse = userService.validateUser(loginRequestDTO);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RefreshRequest refreshRequestDTO = new RefreshRequest(loginId, tokenResponse.getRefreshToken());

        //when
        TokenResponse newToken = userService.refreshAccessToken(refreshRequestDTO);


        //then
        assertNotNull(newToken.getAccessToken());
        assertNotEquals(tokenResponse.getAccessToken(), newToken.getAccessToken());
        assertEquals(tokenResponse.getRefreshToken(), newToken.getRefreshToken());

    }

    @Test
    @DisplayName("리프레쉬 토큰 재발급 샐패")
    public void refreshAccessToken_fail(){
        //given
        String loginId = "testuser";
        String password = "1234";
        LoginRequest loginRequestDTO = new LoginRequest(loginId, password);

        TokenResponse tokenResponse = userService.validateUser(loginRequestDTO);

        //when
        redisTemplate.delete(loginId);

        RefreshRequest refreshRequestDTO = new RefreshRequest(loginId, tokenResponse.getRefreshToken());


        //then
        assertThrows(UnauthorizedException.class,()->userService.refreshAccessToken(refreshRequestDTO));

    }

    @Test
    @DisplayName("로그아웃")
    public void logout_success() {
        //given
        String loginId = "testuser";
        String password = "1234";
        LoginRequest loginRequestDTO = new LoginRequest(loginId, password);

        TokenResponse tokenResponse = userService.validateUser(loginRequestDTO);

        //when
        userService.logout(tokenResponse.getAccessToken());

        //then
        assertNull(refreshTokenService.getRefreshToken(loginId));

    }

}
