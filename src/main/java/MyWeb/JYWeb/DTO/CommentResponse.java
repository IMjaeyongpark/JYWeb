package MyWeb.JYWeb.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String content;
    private String userNickname;
    private LocalDateTime createdAt;
}

