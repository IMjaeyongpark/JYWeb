package MyWeb.JYWeb.service;

import MyWeb.JYWeb.DTO.UserDTO;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MainService {


    private final UserRepository userRepository;

    MainService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    //스프링 실행 확인
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("good!");
    }

    //회원가입 사용자 정보 저장
    public ResponseEntity newUser(UserDTO form){

        User user = new User();
        user.setLoginId(form.getLoginId());
        user.setPassword(form.getPassword());
        user.setNickname(form.getNickname());

        userRepository.save(user);


        return ResponseEntity.ok().build();
    }
}
