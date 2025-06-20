package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.CommentCreateRequestDTO;
import MyWeb.JYWeb.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }

    public void createComment(CommentCreateRequestDTO commentCreateRequestDTO){

    }

}
