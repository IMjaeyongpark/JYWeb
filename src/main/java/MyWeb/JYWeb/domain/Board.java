package MyWeb.JYWeb.domain;

import MyWeb.JYWeb.DTO.board.BoardCreateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "board")
public class Board {

    public Board(String title,String content, User user){
        this.title = title;
        this.content = content;
        this.user = user;
    }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardId")
    private Long boardId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 작성자

    @Column(nullable = false)
    private int viewCount = 0;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = java.time.LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = java.time.LocalDateTime.now();

    @Column
    private LocalDateTime deletedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadFile> files = new ArrayList<>();


    public static Board from(BoardCreateRequest dto, User user) {
        return new Board(dto.getTitle(), dto.getContent(), user);
    }

}

