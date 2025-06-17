package MyWeb.JYWeb.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequestDTO {
    private String loginId;
    private String password;
}
