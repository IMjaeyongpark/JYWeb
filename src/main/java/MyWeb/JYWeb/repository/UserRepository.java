package MyWeb.JYWeb.repository;

import MyWeb.JYWeb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //loginId 중복 확인
    boolean existsByLoginId(String loginId);

    //Nickname 중복 확인
    boolean existsByNickname(String nickname);

    //loginId로 사용자 찾기
    Optional<User> findByLoginId(String LoginId);
}
