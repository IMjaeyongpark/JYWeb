package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.DTO.UserDTO;
import MyWeb.JYWeb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {



    private final UserService userService;


    public UserController(UserService userService){
        this.userService = userService;
    }

    //스프링 실행 확인
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return userService.test();
    }

    //회원가입 사용자 정보 저장
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO form){

        userService.registerUser(form);

        return ResponseEntity.ok("회원가입 성공");
    }

    //아이디 중복 확인
    @GetMapping("/checkId")
    public ResponseEntity<Boolean> checkLoginIdDuplicate(@RequestParam String loginId) {
        boolean isDuplicate = userService.isLoginIdDuplicate(loginId);
        return ResponseEntity.ok(isDuplicate); // true면 중복
    }

    @GetMapping("/checkNickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(isDuplicate); // true면 중복
    }


}
