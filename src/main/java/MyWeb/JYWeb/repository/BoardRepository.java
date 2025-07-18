package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.DTO.board.BoardResponse;
import MyWeb.JYWeb.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long> {


    //삭제되지 않은 게시물 가져오기
    @Query("SELECT new MyWeb.JYWeb.DTO.board.BoardResponse(b.boardId, b.title, u.nickname, b.viewCount, b.createdAt) " +
            "FROM Board b JOIN b.user u " +
            "WHERE b.deletedAt IS NULL")
    Page<BoardResponse> findAllByDeletedAtIsNull(Pageable pageable);



//    //게시물 내용 가져오기
//    @Query("SELECT new MyWeb.JYWeb.DTO.board.BoardDetailResponse(b.boardId, b.title, b.content, u.nickname, u.loginId, " +
//            " b.viewCount, b.createdAt,b.updatedAt,b.deletedAt, b.files) " +
//            "FROM Board b JOIN b.user u " +
//            "WHERE b.boardId = :boardId")
//    BoardDetailResponse findByBoardId(@Param("boardId") Long boardId);

    //특정 사용자의 삭제되지 않은 게시물 가져오기
    @Query("SELECT new MyWeb.JYWeb.DTO.board.BoardResponse(b.boardId, b.title, u.nickname, b.viewCount, b.createdAt) " +
            "FROM Board b JOIN b.user u " +
            "WHERE u.userId = :userId and b.deletedAt IS NULL")
    Page<BoardResponse> findAllByUserIdAndDeletedAtIsNull(@Param("userId") Long userId, Pageable pageable);

    //boardId 값으로 게시글 가져오기
    @Query("SELECT new MyWeb.JYWeb.DTO.board.BoardResponse(b.boardId, b.title, u.nickname, b.viewCount, b.createdAt) " +
            "FROM Board b JOIN b.user u " +
            "WHERE b.deletedAt IS NULL AND b.boardId IN :ids " +
            "ORDER BY b.createdAt DESC")
    List<BoardResponse> findBoardResponsesByBoardIdIn(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.boardId = :boardId")
    int incrementViewCount(@Param("boardId") Long boardId);

}
