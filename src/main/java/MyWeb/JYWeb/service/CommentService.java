package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.CommentCreateRequest;
import MyWeb.JYWeb.Util.JwtUtil;
import MyWeb.JYWeb.domain.Board;
import MyWeb.JYWeb.domain.Comment;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.exception.custom.BoardNotFoundException;
import MyWeb.JYWeb.exception.custom.CommentNotFoundException;
import MyWeb.JYWeb.exception.custom.ValidateLoginException;
import MyWeb.JYWeb.repository.BoardRepository;
import MyWeb.JYWeb.repository.CommentRepository;
import MyWeb.JYWeb.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class CommentService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, BoardRepository boardRepository) {
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

}
