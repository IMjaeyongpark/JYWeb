package MyWeb.JYWeb.service;

import MyWeb.JYWeb.DTO.LoginRequestDTO;
import MyWeb.JYWeb.DTO.TokenResponse;
import MyWeb.JYWeb.DTO.UserDTO;
import MyWeb.JYWeb.Util.JwtUtil;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.exception.custom.DuplicateLoginIdException;
import MyWeb.JYWeb.exception.custom.UnauthorizedException;
import MyWeb.JYWeb.exception.custom.ValidateLoginException;
import MyWeb.JYWeb.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserService {

    @Value("${jwt.secret}")
    private String secretKey;

    //30분
    private Long accessTokenExpiredMs = 1000 * 60 * 30L;

    //1일
    private Long refreshTokenExpiredMs = 1000 * 60 * 60 * 24L;


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService refreshTokenService;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    //스프링 실행 확인
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("good!");
    }

    //회원가입 사용자 정보 저장
    public void registerUser(UserDTO form) {

        log.info("회원가입 시도: {}", form.getLoginId());

        if (userRepository.existsByLoginId(form.getLoginId()) || userRepository.existsByNickname(form.getNickname())) {
            throw new DuplicateLoginIdException();
        }

        User user = new User();
        user.setLoginId(form.getLoginId());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setNickname(form.getNickname());

        userRepository.save(user);

        log.info("회원가입 완료: {}", form.getLoginId());

    }

    //사용자 정보 확인
    public TokenResponse validateUser(LoginRequestDTO loginRequest) {

        Optional<User> user = userRepository.findByLoginId(loginRequest.getLoginId());

        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            String accessToken = JwtUtil.creatAccessToken(user.get().getLoginId(), secretKey, accessTokenExpiredMs);
            String refreshToken = JwtUtil.createRefreshToken(secretKey, refreshTokenExpiredMs);

            //Redis에 refresh token 저장
            refreshTokenService.saveRefreshToken(user.get().getLoginId(), refreshToken, refreshTokenExpiredMs);

            return new TokenResponse(accessToken, refreshToken, loginRequest.getLoginId(), user.get().getNickname());
        }

        throw new ValidateLoginException();
    }


    //엑세스 토큰 재발급
    public TokenResponse refreshAccessToken(String loginId, String refreshToken) {

        String savedToken = refreshTokenService.getRefreshToken(loginId);

        if (savedToken != null && savedToken.equals(refreshToken)) {

            // 유저 정보 조회
            User user = userRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new ValidateLoginException("사용자를 찾을 수 없습니다."));

            // 액세스 토큰 새로 발급
            String newAccessToken = JwtUtil.creatAccessToken(loginId, secretKey, accessTokenExpiredMs);

            return new TokenResponse(newAccessToken, refreshToken, user.getLoginId(), user.getNickname());
        }

        throw new UnauthorizedException();
    }

    //리프레시 토큰 삭제
    public void logout(String accessToken){
        String loginId = JwtUtil.getLoginId(accessToken, secretKey);
        refreshTokenService.deleteRefreshToken(loginId);
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
