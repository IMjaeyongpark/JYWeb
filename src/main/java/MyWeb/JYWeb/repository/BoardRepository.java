package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.DTO.BoardResponse;
import MyWeb.JYWeb.DTO.CommentResponse;
import MyWeb.JYWeb.domain.Board;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {


    //삭제되지 않은 게시물 가져오기
    @Query("SELECT new MyWeb.JYWeb.DTO.BoardResponse(b.boardId, b.title, u.nickname, b.viewCount, b.createdAt) " +
            "FROM Board b JOIN b.user u " +
            "WHERE b.deletedAt IS NULL")
    Page<BoardResponse> findByDeletedAtIsNull(Pageable pageable);



}
