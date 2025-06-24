package MyWeb.JYWeb.service;


import MyWeb.JYWeb.domain.Board;
import MyWeb.JYWeb.domain.Comment;
import MyWeb.JYWeb.domain.User;
import MyWeb.JYWeb.repository.BoardRepository;
import MyWeb.JYWeb.repository.CommentRepository;
import MyWeb.JYWeb.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootTest
public class InsertDataTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("테스트용 유저 등록 (영구 삽입)")
    void initUser() {
        if (userRepository.findByLoginId("testuser").isEmpty()) {
            User user = new User();
            user.setLoginId("testuser");
            user.setPassword(passwordEncoder.encode("1234"));
            user.setNickname("테스트계정");
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("테스트용 게시물 등록 (영구 삽입)")
    void initBoard() {

        String content = "테스트를 위한 게시물 입니다\n 테스트 게시물 :";

        User user = userRepository.findByLoginId("testuser").get();

        for (int i = 0; i < 30; i++) {
            Board board = new Board();
            board.setTitle("test title" + i);
            board.setContent(content + i);
            board.setUser(user);

            boardRepository.save(board);
        }
    }

    @Test
    @DisplayName("테스트용 게시물 등록 (영구 삽입)")
    void initComment() {

        String content = "테스트를 위한 댓글 입니다.";

        User user = userRepository.findByLoginId("testuser").get();
        List<Board> list = boardRepository.findAll();

        for (int i = 0; i < list.size(); i++) {
            for(int j = 0; j < 5;j++){
                Comment comment = new Comment();
                comment.setBoard(list.get(i));
                comment.setUser(user);
                comment.setContent(content);

                commentRepository.save(comment);
                if(j == 0){
                    Comment dcomment = new Comment();
                    dcomment.setBoard(list.get(i));
                    dcomment.setUser(user);
                    dcomment.setContent(content);
                    dcomment.setParent(comment);
                    commentRepository.save(dcomment);
                }
            }
        }

    }
}
