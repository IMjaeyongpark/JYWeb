package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.domain.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //논리적 삭제(soft delete) - 댓글 전체를 deletedAt 업데이트
    @Modifying
    @Query("UPDATE Comment c SET c.deletedAt = :now WHERE c.board.boardId = :boardId")
    int softDeleteAllByBoard(@Param("boardId") Long boardId, @Param("now") LocalDateTime now);

    //논리적 삭제(soft delete) - 대댓글 전체를 deletedAt 업데이트
    @Modifying
    @Query("UPDATE Comment c SET c.deletedAt = :now WHERE c.parent.commentId = :commentId")
    int softDeleteAllByCommentId(@Param("commentId") Long commentId, @Param("now") LocalDateTime now);

    //물리적 삭제(진짜 삭제)
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.board.boardId = :boardId")
    int deleteAllByBoard(@Param("boardId") Long boardId);

    //물리적 삭제(진짜 삭제)
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.parent.commentId = :commentId")
    int deleteAllByCommentId(@Param("commentId") Long commentId);

}
