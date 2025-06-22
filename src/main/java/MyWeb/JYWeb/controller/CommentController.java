package MyWeb.JYWeb.controller;


import MyWeb.JYWeb.DTO.CommentCreateRequest;
import MyWeb.JYWeb.DTO.CommentResponse;
import MyWeb.JYWeb.service.CommentService;
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


    //댓글 등록
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

    //댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, HttpServletRequest request){

        String accessToken = request.getHeader("Authorization");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        commentService.deleteComment(commentId, accessToken);

        return ResponseEntity.ok("삭제 완료");
    }

    //게시글 댓글 가져오기
    @GetMapping("/get")
    public ResponseEntity<List<CommentResponse>> getComments(@RequestParam("boardId") Long boardId,
                                                             @RequestParam("pageNum") int pageNum,
                                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){


        List<CommentResponse> commentResponsePage = commentService.getComments(boardId, pageNum, pageSize);

        return ResponseEntity.ok(commentResponsePage);
    }

}
