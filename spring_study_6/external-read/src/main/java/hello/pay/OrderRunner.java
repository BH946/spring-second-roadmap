package hello.pay;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ApplicationRunner 인터페이스를 사용하면 스프링은 빈 초기화가 모두 끝나고 애플리케이션 로딩이 완료되는 시점에 run(args) 메서드를 호출
 */
@Component
@RequiredArgsConstructor
public class OrderRunner implements ApplicationRunner {
    private final OrderService orderService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        orderService.order(1000);
    }
}