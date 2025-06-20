package MyWeb.JYWeb.service;


import MyWeb.JYWeb.DTO.BoardCreateRequestDTO;
import MyWeb.JYWeb.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    public void createBoard(BoardCreateRequestDTO boardCreateRequestDTO){


    }
}
