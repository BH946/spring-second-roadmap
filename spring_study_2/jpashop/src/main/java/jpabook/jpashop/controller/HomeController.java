package jpabook.jpashop.controller;


// 웹계층 개발 시작

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j // => 로그확인 위해
public class HomeController {

    @RequestMapping("/") // 당연히 @GetMapping 을 사용해도 된다는점 참고
    public String home() {
        log.info("home controller"); // @Slf4j 통해서 로그 사용
        return "home";
    }


}
