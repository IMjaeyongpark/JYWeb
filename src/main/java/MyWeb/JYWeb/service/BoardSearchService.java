package MyWeb.JYWeb.service;

import MyWeb.JYWeb.DTO.BoardResponse;
import MyWeb.JYWeb.domain.Board;
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
        //ES에서 boardId 페이징 조회
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending());
        Page<BoardDocument> esPage = boardEsRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);

        List<Long> ids = esPage.getContent().stream()
                .map(BoardDocument::getBoardId)
                .toList();

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        //DB에서 최신 정보 조회
        List<BoardResponse> dbList = boardRepository.findBoardResponsesByBoardIdIn(ids);

        //ES 순서대로 정렬
        Map<Long, BoardResponse> map = dbList.stream()
                .collect(Collectors.toMap(BoardResponse::getBoardId, b -> b));

        List<BoardResponse> sorted = ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();

        //Page로 반환
        return new PageImpl<>(sorted, pageable, esPage.getTotalElements());
    }

}
