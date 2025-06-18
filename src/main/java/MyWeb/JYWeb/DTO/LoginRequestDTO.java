package MyWeb.JYWeb.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String loginId;
    private String password;

    public LoginRequestDTO() {}

    public LoginRequestDTO(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
