package MyWeb.JYWeb.service;

import MyWeb.JYWeb.domain.Board;
import MyWeb.JYWeb.domain.BoardDocument;
import MyWeb.JYWeb.repository.BoardElasticsearchRepository;
import MyWeb.JYWeb.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
public class BoardSearchService {
    private final BoardElasticsearchRepository boardEsRepository;
    private final BoardRepository boardRepository;


    // 게시글 목록
    public Page<BoardDocument> searchByKeyword(String keyword, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending());
        Page<BoardDocument> result = boardEsRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return result;
    }

}
