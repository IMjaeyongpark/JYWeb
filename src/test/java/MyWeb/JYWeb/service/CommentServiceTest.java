package MyWeb.JYWeb.service;

import MyWeb.JYWeb.DTO.CommentCreateRequest;
import MyWeb.JYWeb.DTO.LoginRequest;
import MyWeb.JYWeb.DTO.TokenResponse;
import MyWeb.JYWeb.repository.BoardRepository;
import MyWeb.JYWeb.repository.CommentRepository;
import MyWeb.JYWeb.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CommentServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;



}
