package MyWeb.JYWeb.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class BoardDetailResponse {
    private Long boardId;

    private String title;

    private String content;

    private String userNickname;

    private int viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

}
