package MyWeb.JYWeb.DTO.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardLikeResponse {
    private boolean liked;
    private long likeCount;
}
