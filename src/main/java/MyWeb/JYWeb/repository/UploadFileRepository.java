package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
}
