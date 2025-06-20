package MyWeb.JYWeb.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequestDTO {
    private String loginId;
    private String refreshToken;

    public RefreshRequestDTO() {
    }

    public RefreshRequestDTO(String loginId, String refreshToken) {
        this.loginId = loginId;
        this.refreshToken = refreshToken;
    }


}