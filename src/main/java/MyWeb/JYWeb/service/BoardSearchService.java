package MyWeb.JYWeb.service;

import MyWeb.JYWeb.DTO.board.BoardResponse;
import MyWeb.JYWeb.domain.BoardDocument;
import MyWeb.JYWeb.repository.BoardElasticsearchRepository;
import MyWeb.JYWeb.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardSearchService {
    private final BoardElasticsearchRepository boardEsRepository;

    private final BoardRepository boardRepository;


    //게시글 검색
    public Page<BoardResponse> searchByKeyword(String keyword, int pageNum, int pageSize) {
        // 1. ES에서 boardId 페이징 조회
        Pageable esPageable = PageRequest.of(pageNum, pageSize);
        Page<BoardDocument> esPage = boardEsRepository.findByTitleContainingOrContentContaining(keyword, keyword, esPageable);

        List<Long> ids = esPage.getContent().stream()
                .map(BoardDocument::getBoardId)
                .toList();

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), esPageable, 0);
        }

        // 2. DB에서 boardId로 최신순 정렬해서 BoardResponse 리스트로 조회
        List<BoardResponse> dbList = boardRepository.findBoardResponsesByBoardIdIn(ids);

        // 3. ES 순서대로 매핑
        Map<Long, BoardResponse> map = dbList.stream()
                .collect(Collectors.toMap(BoardResponse::getBoardId, b -> b));
        List<BoardResponse> sorted = ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(sorted, esPageable, esPage.getTotalElements());
    }


}
