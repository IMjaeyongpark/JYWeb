package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.DTO.BoardCreateRequest;
import MyWeb.JYWeb.DTO.BoardDetailResponse;
import MyWeb.JYWeb.DTO.BoardResponse;
import MyWeb.JYWeb.DTO.CommentResponse;
import MyWeb.JYWeb.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
@Slf4j
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    //게시글 등록
    @PostMapping("/create")
    public ResponseEntity<String> createBoard(@RequestBody BoardCreateRequest boardCreateRequest, HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        boardService.createBoard(boardCreateRequest, accessToken);

        return ResponseEntity.ok("등록 완료");
    }

    //게시글 삭제
    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId, HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        boardService.deleteBoard(boardId, accessToken);

        return ResponseEntity.ok("삭제 완료");
    }

    //게시글 목록 가져오기
    @GetMapping("/get")
    public ResponseEntity<Page<BoardResponse>> getBoard(@RequestParam("pageNum") int pageNum,
                                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Page<BoardResponse> boardResponsePage = boardService.getBoard(pageNum, pageSize);

        return ResponseEntity.ok(boardResponsePage);

    }

    //게시글 상세내용 가져오기
    @GetMapping("/getDetail")
    public ResponseEntity<BoardDetailResponse> getBoardDetail(@RequestParam("boardId") Long boardId) {

        BoardDetailResponse boardDetailResponse = boardService.getBoardDetail(boardId);
        return ResponseEntity.ok(boardDetailResponse);
    }

    //사용자 게시글 목록 가져오기
    @GetMapping("/getUser")
    public ResponseEntity<Page<BoardResponse>> getUserBoard(@RequestParam("pageNum") int pageNum,
                                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                            HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Page<BoardResponse> boardResponses = boardService.getUserBoard(accessToken,pageNum, pageSize);
        return ResponseEntity.ok(boardResponses);

    }
}
