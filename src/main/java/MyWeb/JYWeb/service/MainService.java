package MyWeb.JYWeb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    //스프링 실행 확인
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("good!");
    }
}
