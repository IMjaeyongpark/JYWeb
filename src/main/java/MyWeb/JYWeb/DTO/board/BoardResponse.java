package MyWeb.JYWeb.DTO.board;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BoardResponse {

    private Long boardId;

    private String title;

    private String userNickname;

    private int viewCount;

    private LocalDateTime createdAt;

}
