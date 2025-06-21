package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.BoardCreateRequest;
import MyWeb.JYWeb.Util.JwtUtil;
import MyWeb.JYWeb.domain.Board;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.exception.custom.BoardNotFoundException;
import MyWeb.JYWeb.exception.custom.UnauthorizedException;
import MyWeb.JYWeb.exception.custom.ValidateLoginException;
import MyWeb.JYWeb.repository.BoardRepository;
import MyWeb.JYWeb.repository.CommentRepository;
import MyWeb.JYWeb.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Transactional
@Slf4j
public class BoardService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    public BoardService(BoardRepository boardRepository,
                        UserRepository userRepository,
                        CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    //게시글 등록
    public void createBoard(BoardCreateRequest boardCreateRequest, String accessToken) {

        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        Board board = new Board();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ValidateLoginException("사용자 없음"));

        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());
        board.setUser(user);

        boardRepository.save(board);

        log.info("게시글 등록 : {}", user.getUserId());

    }

    //게시글 삭제
    public void deleteBoard(Long boardId, String accessToken) {

        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException());

        if (!board.getUser().getLoginId().equals(loginId)) {
            throw new UnauthorizedException("삭제 권한이 없습니다.");
        }

        if(board.getDeletedAt() == null) {
            commentRepository.softDeleteAllByBoard(boardId, LocalDateTime.now());
            boardRepository.softDeleteByBoardId(boardId, LocalDateTime.now());
        }

        log.info("게시글 삭제 완료 : {}", board.getBoardId());
    }


}
