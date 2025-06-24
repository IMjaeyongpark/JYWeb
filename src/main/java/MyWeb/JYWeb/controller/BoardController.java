package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.DTO.BoardCreateRequest;
import MyWeb.JYWeb.DTO.BoardDetailResponse;
import MyWeb.JYWeb.DTO.BoardResponse;
import MyWeb.JYWeb.DTO.CommentResponse;
import MyWeb.JYWeb.service.BoardService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<String> deleteBoard(@PathVariable("boardId") Long boardId, HttpServletRequest request) {

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
    public ResponseEntity<BoardDetailResponse> getBoardDetail(@RequestParam("boardId") Long boardId,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {

        // 1. 쿠키에서 이미 본 게시글인지 확인
        boolean increase = true;
        String cookieName = "viewedBoards";
        Cookie[] cookies = request.getCookies();

        String viewed = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    viewed = cookie.getValue();
                    if (viewed.contains("[" + boardId + "]")) {
                        increase = false; // 이미 본 글
                    }
                }
            }
        }

        // 2. 조회수 증가
        if (increase) {
            boardService.increaseViewCount(boardId);
            // 3. 쿠키에 기록
            Cookie newCookie = new Cookie(cookieName, viewed + "[" + boardId + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24); // 1일
            response.addCookie(newCookie);
        }

        // 4. 상세 조회 데이터 리턴
        BoardDetailResponse detail = boardService.getBoardDetail(boardId);
        return ResponseEntity.ok(detail);
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
