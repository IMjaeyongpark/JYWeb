package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.DTO.UserDTO;
import MyWeb.JYWeb.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {


    @Autowired
    private final MainService mainService;

    public MainController(MainService mainService, PasswordEncoder passwordEncoder){
        this.mainService = mainService;
    }

    //스프링 실행 확인
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return mainService.test();
    }

    //회원가입 사용자 정보 저장
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO form){

        mainService.registerUser(form);

        return ResponseEntity.ok("회원가입 성공");
    }
}
