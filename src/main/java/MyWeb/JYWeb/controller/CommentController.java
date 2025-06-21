package MyWeb.JYWeb.controller;


import MyWeb.JYWeb.DTO.CommentCreateRequest;
import MyWeb.JYWeb.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
