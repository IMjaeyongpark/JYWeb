package MyWeb.JYWeb.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String content;
    private String userNickname;
    private String loginId;
    private String createdAt;
    private List<CommentResponse> replies;

    private LocalDateTime deletedAt;

}


