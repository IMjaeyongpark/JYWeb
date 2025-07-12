package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //논리적 삭제(soft delete) - 댓글 전체를 deletedAt 업데이트
    @Modifying
    @Query("UPDATE Comment c SET c.deletedAt = :now WHERE c.board.boardId = :boardId")
    int softDeleteAllByBoard(@Param("boardId") Long boardId, @Param("now") LocalDateTime now);

    //물리적 삭제(진짜 삭제)
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.board.boardId = :boardId")
    int deleteAllByBoard(@Param("boardId") Long boardId);

    //댓글 가져오기 - boardId로 대댓글이 아닌 댓글 생성일 내림차순
    List<Comment> findAllByBoard_BoardIdAndParentIsNullOrderByCreatedAtAsc(Long boardId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.content = :content WHERE c.commentId = :commentId")
    int updateComment(@Param("commentId") Long commentId, @Param("content") String content);

}
