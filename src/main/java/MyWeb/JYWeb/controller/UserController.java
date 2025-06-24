package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.DTO.LoginRequest;
import MyWeb.JYWeb.DTO.RefreshRequest;
import MyWeb.JYWeb.DTO.TokenResponse;
import MyWeb.JYWeb.DTO.RegisterRequest;
import MyWeb.JYWeb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {


    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    //스프링 실행 확인
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return userService.test();
    }

    //회원가입 사용자 정보 저장
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest form) {

        userService.registerUser(form);

        return ResponseEntity.ok("회원가입 성공");
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        log.info("받은 loginId={}, password={}", loginRequest.getLoginId(), loginRequest.getPassword());

        TokenResponse tokenResponse = userService.validateUser(loginRequest);

        log.info("로그인 성공: loginId={} from IP={}", loginRequest.getLoginId(), request.getRemoteAddr());

        return ResponseEntity.ok(tokenResponse);
    }


    //리프레시 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestBody RefreshRequest refreshRequest) {
        TokenResponse tokenResponse = userService.refreshAccessToken(refreshRequest);

        log.info("리프레시 토큰 재발급: {}", refreshRequest.getLoginId());

        return ResponseEntity.ok(tokenResponse);
    }

    //로그아웃
    @DeleteMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request){
        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        userService.logout(accessToken);
        return ResponseEntity.ok().body("로그아웃 완료");
    }

    //아이디 중복 확인
    @GetMapping("/checkId")
    public ResponseEntity<Boolean> checkLoginIdDuplicate(@RequestParam("loginId") String loginId) {
        boolean isDuplicate = userService.isLoginIdDuplicate(loginId);
        return ResponseEntity.ok(isDuplicate); // true면 중복
    }

    //닉네입 중복 확인
    @GetMapping("/checkNickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(isDuplicate); // true면 중복
    }


}
