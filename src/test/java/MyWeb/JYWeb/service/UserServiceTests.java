package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.UserDTO;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.exception.DuplicateLoginIdException;
import MyWeb.JYWeb.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 성공")
    public void register_success() {
        // given
        UserDTO form = new UserDTO();
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

        UserDTO form = new UserDTO();
        form.setLoginId("duplicate");
        form.setPassword("newpass");
        form.setNickname("새유저");

        // when & then
        assertThrows(DuplicateLoginIdException.class, () -> userService.registerUser(form));
    }
}
