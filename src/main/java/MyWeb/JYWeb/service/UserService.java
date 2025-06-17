package MyWeb.JYWeb.service;

import MyWeb.JYWeb.DTO.UserDTO;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.exception.DuplicateLoginIdException;
import MyWeb.JYWeb.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    //스프링 실행 확인
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("good!");
    }

    //회원가입 사용자 정보 저장
    public void registerUser(UserDTO form){

        if (userRepository.existsByLoginId(form.getLoginId())) {
            throw new DuplicateLoginIdException();
        }

        User user = new User();
        user.setLoginId(form.getLoginId());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setNickname(form.getNickname());

        userRepository.save(user);

    }

    //아이디 중복 확인
    public boolean isLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    //닉네임 중복 확인
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
