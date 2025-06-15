package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.DTO.UserDTO;
import MyWeb.JYWeb.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    private final MainService mainService;

    public MainController(MainService mainService){
        this.mainService = mainService;
    }

    //스프링 실행 확인
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return mainService.test();
    }

    //회원가입 사용자 정보 저장
    @PostMapping("/signup")
    public ResponseEntity newUser(@RequestBody UserDTO form){
        return mainService.newUser(form);
    }
}
