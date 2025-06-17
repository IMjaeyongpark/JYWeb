package MyWeb.JYWeb.service;


import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class InsertDataTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("테스트용 유저 등록 (영구 삽입)")
    void initUser() {
        if (userRepository.findByLoginId("testuser").isEmpty()) {
            User user = new User();
            user.setLoginId("testuser");
            user.setPassword(passwordEncoder.encode("1234"));
            user.setNickname("테스트계정");
            userRepository.save(user);
        }
    }
}
