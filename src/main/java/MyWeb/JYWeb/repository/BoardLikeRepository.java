package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.domain.Board;
import MyWeb.JYWeb.domain.BoardLike;
import MyWeb.JYWeb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByBoardAndUser(Board board, User user);
    long countByBoard(Board board);
    void deleteByBoardAndUser(Board board, User user);
}