package MyWeb.JYWeb.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "board_file")
public class UploadFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uploadName;
    private String originalName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


    public UploadFile(String uploadName, String originalName, Board board) {
        this.uploadName = uploadName;
        this.originalName = originalName;
        this.board = board;
    }
}

