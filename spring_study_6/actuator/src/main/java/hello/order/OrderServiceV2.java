package hello.order;

import io.micrometer.core.annotation.Counted;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class OrderServiceV2 implements OrderService {
    private AtomicInteger stock = new AtomicInteger(100); // 100개 재고로 세팅

    @Counted("my.order")
    @Override
    public void order() {
        log.info("주문");
        stock.decrementAndGet(); // AtomicInteger 클래스가 제공
    }
    @Counted("my.order")
    @Override
    public void cancel() {
        log.info("취소");
        stock.incrementAndGet();
    }

    @Override
    public AtomicInteger getStock() {
        return stock;
    }

}
