package MyWeb.JYWeb.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String content;
    private String userNickname;
    private String createdAt;
    private List<CommentResponse> replies;
}


