package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.BoardCreateRequest;
import MyWeb.JYWeb.DTO.CommentCreateRequest;
import MyWeb.JYWeb.DTO.LoginRequest;
import MyWeb.JYWeb.DTO.TokenResponse;
import MyWeb.JYWeb.domain.Board;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.repository.BoardRepository;
import MyWeb.JYWeb.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;
    @Autowired
    private UserService userService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    private TokenResponse loginAsTestUser() {
        return userService.validateUser(new LoginRequest("testuser", "1234"));
    }

    @Test
    @DisplayName("게시글 작성 성공")
    public void createBoard_success() {
        //given
        String title = "test title";
        String content = "test content";

        TokenResponse tokenResponse = loginAsTestUser();

        BoardCreateRequest boardCreateRequest = new BoardCreateRequest(title, content);

        //when

        Long boardId = boardService.createBoard(boardCreateRequest,tokenResponse.getAccessToken());

        //then

        Board saved = boardRepository.findById(boardId).orElseThrow();
        assertEquals("test title", saved.getTitle());
        assertEquals("test content", saved.getContent());
        assertEquals("testuser", saved.getUser().getLoginId());

    }



}
