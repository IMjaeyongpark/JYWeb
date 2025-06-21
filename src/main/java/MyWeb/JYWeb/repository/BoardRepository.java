package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.domain.Board;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    //삭제되지 않은 게시글 찾기
    List<Board> findByDeletedAtIsNull();

    // 1) 논리적 삭제(soft delete) - 게시글 deletedAt 업데이트
    @Modifying
    @Query("UPDATE Board b SET b.deletedAt = :now WHERE b.boardId = :boardId")
    int softDeleteByBoardId(@Param("boardId") Long boardId, @Param("now") LocalDateTime now);

    // 2) 물리적 삭제(진짜 삭제)
    @Modifying
    @Query("DELETE FROM Board b WHERE b.boardId = :boardId")
    int deleteByBoardId(@Param("boardId") Long boardId);

}
