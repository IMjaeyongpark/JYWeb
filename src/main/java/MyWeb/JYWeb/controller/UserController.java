package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.DTO.LoginRequest;
import MyWeb.JYWeb.DTO.RefreshRequest;
import MyWeb.JYWeb.DTO.TokenResponse;
import MyWeb.JYWeb.DTO.RegisterRequest;
import MyWeb.JYWeb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;


    @Operation(
            summary = "API 테스트",
            description = "API 통신 테스트입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "통신 성공")
            }
    )
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return userService.test();
    }

    @Operation(
            summary = "회원가입",
            description = "회원 정보를 입력받아 새로운 사용자를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "409", description = "입력값 오류 또는 중복된 ID/닉네임")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest form) {

        userService.registerUser(form);

        return ResponseEntity.ok("회원가입 성공");
    }

    @Operation(
            summary = "로그인",
            description = "로그인 ID와 비밀번호를 받아 사용자를 인증하고 토큰을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 오류")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        log.info("받은 loginId={}, password={}", loginRequest.getLoginId(), loginRequest.getPassword());

        TokenResponse tokenResponse = userService.validateUser(loginRequest);

        log.info("로그인 성공: loginId={} from IP={}", loginRequest.getLoginId(), request.getRemoteAddr());

        return ResponseEntity.ok(tokenResponse);
    }


    @Operation(
            summary = "액세스 토큰 재발급",
            description = "리프레시 토큰을 이용하여 새로운 액세스 토큰을 발급받습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "재발급 성공"),
                    @ApiResponse(responseCode = "401", description = "리프레시 토큰 없음 또는 유효하지 않음")
            }
    )

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestBody RefreshRequest refreshRequest) {
        TokenResponse tokenResponse = userService.refreshAccessToken(refreshRequest);

        log.info("리프레시 토큰 재발급: {}", refreshRequest.getLoginId());

        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
            summary = "로그아웃",
            description = "리프레시 토큰을 만료시켜 로그아웃 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
                    @ApiResponse(responseCode = "401", description = "엑세스 토큰 유효하지 않음")
            }
    )
    @DeleteMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        userService.logout(accessToken);
        return ResponseEntity.ok().body("로그아웃 완료");
    }


    @CrossOrigin(origins = "*")
    @Operation(
            summary = "아이디 중복 확인",
            description = "입력한 로그인 ID가 이미 존재하는지 확인합니다. 반환값이 true이면 중복된 ID입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "중복 여부 확인 성공 (true: 중복, false: 사용 가능)")
            }
    )
    @GetMapping("/checkId")
    public ResponseEntity<Boolean> checkLoginIdDuplicate(@RequestParam("loginId") String loginId) {
        boolean isDuplicate = userService.isLoginIdDuplicate(loginId);
        return ResponseEntity.ok(isDuplicate); // true면 중복
    }

    @Operation(
            summary = "낙네임 중복 확인",
            description = "입력한 닉네임이 이미 존재하는지 확인합니다. 반환값이 true이면 중복된 ID입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "중복 여부 확인 성공 (true: 중복, false: 사용 가능)")
            }
    )
    @GetMapping("/checkNickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(isDuplicate); // true면 중복
    }


}
