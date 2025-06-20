package MyWeb.JYWeb.controller;


import MyWeb.JYWeb.DTO.BoardCreateRequestDTO;
import MyWeb.JYWeb.DTO.CommentCreateRequestDTO;
import MyWeb.JYWeb.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @PostMapping("/create")
    public ResponseEntity createComment(@RequestBody CommentCreateRequestDTO commentCreateRequestDTO){

        commentService.createComment(commentCreateRequestDTO);

        return ResponseEntity.ok().build();
    }

}
