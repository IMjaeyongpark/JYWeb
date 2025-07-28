package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.domain.CommentLike;
import MyWeb.JYWeb.domain.Comment;
import MyWeb.JYWeb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
    long countByComment(Comment comment);
    void deleteByCommentAndUser(Comment comment, User user);
}
