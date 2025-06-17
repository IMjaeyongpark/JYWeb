package MyWeb.JYWeb.DTO;

import lombok.Getter;

@Getter
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String loginId;
    private String nickname;

    public TokenResponse(String accessToken, String refreshToken, String loginId, String nickname) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.loginId = loginId;
        this.nickname = nickname;
    }
}
