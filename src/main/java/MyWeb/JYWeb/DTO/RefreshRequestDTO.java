package MyWeb.JYWeb.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
public class RefreshRequestDTO {
    private String loginId;
    private String refreshToken;
}