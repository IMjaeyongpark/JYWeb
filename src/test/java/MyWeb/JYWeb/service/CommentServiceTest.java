package MyWeb.JYWeb.service;

import MyWeb.JYWeb.repository.BoardRepository;
import MyWeb.JYWeb.repository.CommentRepository;
import MyWeb.JYWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CommentServiceTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;
}
