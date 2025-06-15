package MyWeb.JYWeb.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//사용자 정보 DTO
@Getter
@Setter
public class UserDTO {
    private Long userId;

    private String loginId;

    private String password;

    private String nickname;

    private LocalDateTime createdAt;

}
