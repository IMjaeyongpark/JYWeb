package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.CommentCreateRequest;
import MyWeb.JYWeb.DTO.CommentResponse;
import MyWeb.JYWeb.Util.JwtUtil;
import MyWeb.JYWeb.domain.Board;
import MyWeb.JYWeb.domain.Comment;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.exception.custom.BoardNotFoundException;
import MyWeb.JYWeb.exception.custom.CommentNotFoundException;
import MyWeb.JYWeb.exception.custom.UnauthorizedException;
import MyWeb.JYWeb.exception.custom.ValidateLoginException;
import MyWeb.JYWeb.repository.BoardRepository;
import MyWeb.JYWeb.repository.CommentRepository;
import MyWeb.JYWeb.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CommentService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository,
                          BoardRepository boardRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }


    //댓글 등록
    public void createComment(CommentCreateRequest commentCreateRequest, String accessToken) {

        Comment comment = new Comment();

        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ValidateLoginException("사용자 없음"));

        Board board = boardRepository.findById(commentCreateRequest.getBoardId())
                .orElseThrow(() -> new BoardNotFoundException());


        comment.setUser(user);
        comment.setBoard(board);
        comment.setContent(commentCreateRequest.getContent());

        if(commentCreateRequest.getParentId() != null){
            Comment parentComment = commentRepository.findById(commentCreateRequest.getParentId())
                    .orElseThrow(() -> new CommentNotFoundException("댓글(부모)이 존재하지 않습니다."));
            comment.setParent(parentComment);
        }

        commentRepository.save(comment);

        log.info("댓글 작성 등록: userId = {} boardId = {}", user.getUserId(), board.getBoardId());

    }

    //댓글 삭제
    public void deleteComment(Long commentId, String accessToken) {
        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException());



        if (!comment.getUser().getLoginId().equals(loginId)) {
            throw new UnauthorizedException("삭제 권한이 없습니다.");
        }

        if(comment.getDeletedAt() == null) {
            comment.setDeletedAt(LocalDateTime.now());
            commentRepository.save(comment);
        }


        log.info("댓글 삭제 완료 : {}", comment.getCommentId());
    }

    // Comment -> CommentResponse로 변환
    public CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getCreatedAt().toString(),
                comment.getChildren().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList())
        );
    }

    // 서비스에서 최상위 댓글만 조회 후, 변환해서 반환
    public List<CommentResponse> getComments(Long boardId) {
        List<Comment> parents = commentRepository.findAllByBoard_BoardIdAndParentIsNullOrderByCreatedAtAsc(boardId);
        return parents.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}
