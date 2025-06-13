package MyWeb.JYWeb.controller;

import MyWeb.JYWeb.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    private final MainService mainService;

    public MainController(MainService mainService){
        this.mainService = mainService;
    }

    //스프링 실행 확인
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return mainService.test();
    }
}
