package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.DTO.board.*;
import MyWeb.JYWeb.service.BoardSearchService;
import MyWeb.JYWeb.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/board")
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    private final BoardSearchService boardSearchService;


    @Operation(
            summary = "게시글 등록",
            description = "JWT 토큰이 필요하며, 게시글 제목과 내용을 입력받아 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "등록 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 없음")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<String> createBoard(@RequestPart("board") BoardCreateRequest boardCreateRequest,
                                              @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                              HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        boardService.createBoard(boardCreateRequest, files, accessToken);


        return ResponseEntity.ok("등록 완료");
    }


    @Operation(
            summary = "게시글 삭제",
            description = "JWT 토큰이 필요하며, 게시글 ID를 통해 특정 게시글을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 없음")
            }
    )
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

    @Operation(
            summary = "게시글 목록 조회",
            description = "게시글 목록을 페이지 단위로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            }
    )
    @GetMapping("/get")
    public ResponseEntity<Page<BoardResponse>> getBoard(
            @RequestParam("pageNum") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "direction", defaultValue = "desc") String direction) {


        log.info("sort:" + sort);
        log.info("direction:" + direction);

        Sort.Direction dir = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Page<BoardResponse> boardResponsePage = boardService.getBoard(pageNum, pageSize, sort, dir);

        return ResponseEntity.ok(boardResponsePage);

    }

    @Operation(
            summary = "게시글 목록 검색",
            description = "검색한 게시글 목록을 페이지 단위로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            }
    )
    @GetMapping("/search")
    public Page<BoardResponse> search(
            @RequestParam("keyword") String keyword,
            @RequestParam("pageNum") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "direction", defaultValue = "desc") String direction
    ) {
        log.info("검색어: " + keyword);

        Sort.Direction dir = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        return boardSearchService.searchByKeyword(keyword, pageNum, pageSize, sort, dir);
    }

    @Operation(
            summary = "게시글 상세 조회",
            description = "게시글 ID를 통해 특정 게시글의 내용을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            }
    )
    @GetMapping("/getDetail")
    public ResponseEntity<BoardDetailResponse> getBoardDetail(@RequestParam("boardId") Long boardId,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {

        //쿠키에서 이미 본 게시글인지 확인
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

        //조회수 증가
        if (increase) {
            boardService.increaseViewCount(boardId);
            //쿠키에 기록
            Cookie newCookie = new Cookie(cookieName, viewed + "[" + boardId + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24); // 1일
            response.addCookie(newCookie);
        }

        //로그인 확인
        String accessToken = null;
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            accessToken = header.substring(7);
        }

        //상세 조회 데이터 리턴
        BoardDetailResponse detail = boardService.getBoardDetail(boardId, accessToken);
        return ResponseEntity.ok(detail);
    }

    @Operation(
            summary = "사용자 게시글 목록 조회",
            description = "JWT 토큰이 필요하며, 특정 사용자의 게시글 목록을 페이지 단위로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 없음")
            }
    )
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

        Page<BoardResponse> boardResponses = boardService.getUserBoard(accessToken, pageNum, pageSize);
        return ResponseEntity.ok(boardResponses);

    }

    @Operation(
            summary = "게시글 수정",
            description = "JWT 토큰이 필요하며, 게시글 ID를 통해 특정 게시글의 제목과 내용을 입력받아 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 없음")
            }
    )
    @PutMapping("/update")
    public ResponseEntity<String> updateBoard(@ModelAttribute BoardUpdateRequest boardUpdateRequest,
                                              @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
                                              HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        boardService.updateBoard(boardUpdateRequest, newFiles, boardUpdateRequest.getDeleteFileNames(), accessToken);

        return ResponseEntity.ok("수정 완료");
    }

    @PostMapping("/{boardId}/like")
    public ResponseEntity<BoardLikeResponse> likeBoard(@PathVariable("boardId") Long boardId, HttpServletRequest request){


        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        BoardLikeResponse response = boardService.likeBoard(boardId, accessToken);


        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{boardId}/like")
    public ResponseEntity<BoardLikeResponse> unlikeBoard(@PathVariable("boardId") Long boardId, HttpServletRequest request){

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        BoardLikeResponse response = boardService.unlikeBoard(boardId,accessToken);

        return ResponseEntity.ok(response);
    }

}
