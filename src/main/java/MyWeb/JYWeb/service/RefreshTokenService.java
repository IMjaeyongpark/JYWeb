package MyWeb.JYWeb.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenService(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    // 저장
    public void saveRefreshToken(String loginId, String refreshToken, long expireMs) {
        redisTemplate.opsForValue().set(loginId, refreshToken, Duration.ofMillis(expireMs));
    }

    // 조회
    public String getRefreshToken(String loginId) {
        return redisTemplate.opsForValue().get(loginId);
    }

    // 삭제
    public void deleteRefreshToken(String loginId) {
        redisTemplate.delete(loginId);
    }
}
