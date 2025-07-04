package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.domain.UploadFile;
import MyWeb.JYWeb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    Optional<UploadFile> findByUploadName(String uploadName);

    List<UploadFile> findByBoard_BoardId(Long boardId);

}
