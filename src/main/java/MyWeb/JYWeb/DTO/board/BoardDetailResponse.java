package MyWeb.JYWeb.DTO.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@AllArgsConstructor
public class BoardDetailResponse {
    private Long boardId;

    private String title;

    private String content;

    private String userNickname;

    private String loginId;

    private int viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private List<String> fileUrls;

}
