package MyWeb.JYWeb.DTO.commnet;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
    private Long boardId;
    private String content;
    @Nullable
    private Long parentId;

}

