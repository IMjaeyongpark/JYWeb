package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.DTO.LoginRequestDTO;
import MyWeb.JYWeb.DTO.RefreshRequestDTO;
import MyWeb.JYWeb.DTO.TokenResponse;
import MyWeb.JYWeb.DTO.UserDTO;
import MyWeb.JYWeb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<String> register(@RequestBody UserDTO form) {

        userService.registerUser(form);

        return ResponseEntity.ok("회원가입 성공");
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {

        TokenResponse tokenResponse = userService.validateUser(loginRequest);

        log.info("로그인 성공: loginId={} from IP={}", loginRequest.getLoginId(), request.getRemoteAddr());

        return ResponseEntity.ok(tokenResponse);
    }

    //리프레시 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestBody RefreshRequestDTO refreshRequestDTO) {
        TokenResponse tokenResponse = userService.refreshAccessToken(
                refreshRequestDTO.getLoginId(),
                refreshRequestDTO.getRefreshToken()
        );

        log.info("리프레시 토큰 재발급: {}", refreshRequestDTO.getLoginId());

        return ResponseEntity.ok(tokenResponse);
    }

    //로그아웃
    @DeleteMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request){
        String accessToken = request.getHeader("Authorization");
        userService.logout(accessToken);
        return ResponseEntity.ok().body("로그아웃 완료");
    }

    //아이디 중복 확인
    @GetMapping("/checkId")
    public ResponseEntity<Boolean> checkLoginIdDuplicate(@RequestParam String loginId) {
        boolean isDuplicate = userService.isLoginIdDuplicate(loginId);
        return ResponseEntity.ok(isDuplicate); // true면 중복
    }

    //닉네입 중복 확인
    @GetMapping("/checkNickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(isDuplicate); // true면 중복
    }


}
