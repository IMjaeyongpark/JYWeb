package MyWeb.JYWeb.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateRequestDTO {

    private String title;

    private String content;

    private Long user_id;

}
