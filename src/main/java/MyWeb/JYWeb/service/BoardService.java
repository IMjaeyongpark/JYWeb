package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.BoardCreateRequest;
import MyWeb.JYWeb.DTO.BoardDetailResponse;
import MyWeb.JYWeb.DTO.BoardResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
            board.setDeletedAt(LocalDateTime.now());
            commentRepository.softDeleteAllByBoard(boardId, LocalDateTime.now());
            boardRepository.save(board);
        }

        log.info("게시글 삭제 완료 : {}", board.getBoardId());
    }

    //게시물 조회
    public Page<BoardResponse> getBoard(int pageNum, int pageSize ){

        Page<BoardResponse> boardResponses = boardRepository.findAllByDeletedAtIsNull(
                PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending()));


        return boardResponses;
    }

    //게시물 내용 조회
    public BoardDetailResponse getBoardDetail(Long boardId){

        BoardDetailResponse boardDetailResponse = boardRepository.findByBoardId(boardId);

        if(boardDetailResponse == null || boardDetailResponse.getDeletedAt() != null){
            throw new BoardNotFoundException("존재하지 않는 게시물입니다.");
        }

        return boardDetailResponse;
    }

    //게시글 조회수 증가
    public void increaseViewCount(Long boardId) {
        boardRepository.incrementViewCount(boardId);
    }



    //사용자 게시글 목록 가져오기
    public Page<BoardResponse> getUserBoard(String accessToken, int pageNum, int pageSize){

        String loginId = JwtUtil.getLoginId(accessToken, secretKey);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ValidateLoginException("사용자 없음"));

        Page<BoardResponse> boardResponses = boardRepository.findAllByUserIdAndDeletedAtIsNull(user.getUserId(),
                PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending()));

        return boardResponses;
    }


}
