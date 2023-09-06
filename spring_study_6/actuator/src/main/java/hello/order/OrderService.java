package hello.order;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 여러가지 버전으로 테스트 해볼것이라 "인터페이스로 구현"
 */
public interface OrderService {
    void order(); // 주문
    void cancel(); // 취소
    AtomicInteger getStock(); // 재고 조회
}
