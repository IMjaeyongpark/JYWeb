package MyWeb.JYWeb.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Test
    @DisplayName("리프레시 토큰 저장 성공")
    public void getRefreshTocken() {
        //given
        String loginId = "testId";
        String refreshToken = "test refreshToken";
        Long refreshTokenExpiredMs = 1000 * 60 * 60 * 24L;


        //when
        refreshTokenService.saveRefreshToken(loginId, refreshToken, refreshTokenExpiredMs);

        //then
        String saved = redisTemplate.opsForValue().get(loginId);
        assertEquals("test refreshToken", saved);

    }

    @Test
    @DisplayName("리프레시 토큰 저장 성공")
    public void deleteRefreshTocken() {
        //given
        String loginId = "testId";
        String refreshToken = "test refreshToken";
        Long refreshTokenExpiredMs = 1000 * 60 * 60 * 24L;
        refreshTokenService.saveRefreshToken(loginId, refreshToken, refreshTokenExpiredMs);


        //when
        refreshTokenService.deleteRefreshToken("testId");

        //then
        String test = redisTemplate.opsForValue().get("testId");
        assertNull(test);

    }


}
