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

    @Test
    @DisplayName("댓글 작성 성공")
    public void createComment_success() {
        //given

        Long boardId = 1L;
        String content = "test";

        CommentCreateRequest commentCreateRequest = new CommentCreateRequest();
        commentCreateRequest.setBoardId(boardId);
        commentCreateRequest.setContent(content);

        String loginId = "testuser";
        String password = "1234";
        LoginRequest loginRequestDTO = new LoginRequest(loginId, password);

        TokenResponse tokenResponse = userService.validateUser(loginRequestDTO);


        //when

        commentService.createComment(commentCreateRequest, tokenResponse.getAccessToken());

        //then



    }

}
