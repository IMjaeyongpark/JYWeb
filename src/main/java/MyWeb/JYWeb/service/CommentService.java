package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.commnet.CommentCreateRequest;
import MyWeb.JYWeb.DTO.commnet.CommentResponse;
import MyWeb.JYWeb.DTO.commnet.CommentUpdateRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;


    //댓글 등록
    public Long createComment(CommentCreateRequest commentCreateRequest, String accessToken) {

        //권한 확인
        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ValidateLoginException("사용자 없음"));

        Board board = boardRepository.findById(commentCreateRequest.getBoardId())
                .orElseThrow(() -> new BoardNotFoundException());

        Comment comment = Comment.form(commentCreateRequest, user, board);


        //부모 댓글 삭제 여부 확인
        if (commentCreateRequest.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentCreateRequest.getParentId())
                    .orElseThrow(() -> new CommentNotFoundException("댓글(부모)이 존재하지 않습니다."));
            comment.setParent(parentComment);
        }

        commentRepository.save(comment);

        log.info("댓글 작성 등록: userId = {} boardId = {}", user.getUserId(), board.getBoardId());

        return comment.getCommentId();
    }

    //댓글 삭제
    public void deleteComment(Long commentId, String accessToken) {

        //권한 확인
        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException());

        if (!comment.getUser().getLoginId().equals(loginId)) {
            throw new UnauthorizedException("삭제 권한이 없습니다.");
        }

        //댓글 소프트 삭제
        if (comment.getDeletedAt() == null) {
            comment.setDeletedAt(LocalDateTime.now());
            commentRepository.save(comment);
        }


        log.info("댓글 삭제 완료 : {}", comment.getCommentId());
    }

    // Comment -> CommentResponse 변환
    public CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getUser().getLoginId(),
                comment.getCreatedAt().toString(),
                comment.getChildren().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                comment.getDeletedAt()
        );
    }

    // 서비스에서 최상위 댓글만 조회 후, 변환해서 반환
    public List<CommentResponse> getComments(Long boardId) {
        List<Comment> parents = commentRepository.findAllByBoard_BoardIdAndParentIsNullOrderByCreatedAtAsc(boardId);
        return parents.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //댓글 수정
    public void updateComment(CommentUpdateRequest commentUpdateRequest, String accessToken) {

        //권한 확인
        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        Comment comment = commentRepository.findById(commentUpdateRequest.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException());

        if (!comment.getUser().getLoginId().equals(loginId)) {
            throw new UnauthorizedException("수정 권한이 없습니다.");
        }

        //댓글 수정
        int updatedRows = commentRepository.updateComment(
                commentUpdateRequest.getCommentId(),
                commentUpdateRequest.getContent()
        );

        if (updatedRows == 0) {
            throw new IllegalStateException("댓글 수정 실패");
        }
    }

}
