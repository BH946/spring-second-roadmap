package hello.pay;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 프로필에 따라 LocalPayClient 또는 ProdPayClient 주입받게 될 것
 */
@Service
@RequiredArgsConstructor
public class OrderService {
    private final PayClient payClient; // LocalPayClient or ProdPayClient

    public void order(int money) {
        payClient.pay(money);
    }
}