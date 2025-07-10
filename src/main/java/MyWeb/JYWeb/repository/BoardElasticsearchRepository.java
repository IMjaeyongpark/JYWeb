package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.domain.BoardDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface BoardElasticsearchRepository extends ElasticsearchRepository<BoardDocument, Long> {
    Page<BoardDocument> findByTitleContainingOrContentContaining(String t, String c, Pageable pageable);

}
