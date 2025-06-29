package MyWeb.JYWeb.controller;


import MyWeb.JYWeb.DTO.BoardUpdateRequest;
import MyWeb.JYWeb.DTO.CommentCreateRequest;
import MyWeb.JYWeb.DTO.CommentResponse;
import MyWeb.JYWeb.DTO.CommentUpdateRequest;
import MyWeb.JYWeb.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }


    @Operation(
            summary = "댓글 작성",
            description = "JWT 토큰이 필요하며, 게시글 ID와 함께 제목 및 내용을 입력받아 댓글을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "등록 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 없음")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<String> createComment(@RequestBody CommentCreateRequest commentCreateRequest, HttpServletRequest request){

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        commentService.createComment(commentCreateRequest, accessToken);

        return ResponseEntity.ok("댓글 작성 완료");
    }

    @Operation(
            summary = "댓글 삭제",
            description = "JWT 토큰이 필요하며, 댓글 ID를 통해 특정 댓글을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 없음")
            }
    )
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId, HttpServletRequest request){

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        commentService.deleteComment(commentId, accessToken);

        return ResponseEntity.ok("삭제 완료");
    }

    @Operation(
            summary = "댓글 조회",
            description = "게시글 ID를 통해 댓글 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            }
    )
    @GetMapping("/get")
    public ResponseEntity<List<CommentResponse>> getComments(@RequestParam("boardId") Long boardId){


        List<CommentResponse> commentResponsePage = commentService.getComments(boardId);

        return ResponseEntity.ok(commentResponsePage);
    }

    @Operation(
            summary = "댓글 수정",
            description = "JWT 토큰이 필요하며, 댓글 ID와 함께 제목 및 내용을 입력받아 댓글을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 없음")
            }
    )
    @PutMapping("/update")
    public ResponseEntity<String> updateBoard(@RequestBody CommentUpdateRequest commentUpdateRequest,
                                              HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        commentService.updateComment(commentUpdateRequest, accessToken);

        return ResponseEntity.ok("수정 완료");
    }

}
