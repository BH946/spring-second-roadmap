package jpabook.jpashop.domain;
import lombok.Getter;
import lombok.Setter;
import jpabook.jpashop.domain.item.Item;
import javax.persistence.*;
@Entity
@Table(name = "order_item") // abc_def 형태로 테이블명 하는중이므로 여기서도 테이블명 변경
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 꼭 LAZY, 다대일&단방향
    @JoinColumn(name = "item_id") // 얘가 주인이라서 FK
    private Item item;      //주문 상품

    @ManyToOne(fetch = FetchType.LAZY) // 꼭 LAZY, 다대일&양방향
    @JoinColumn(name = "order_id") // 얘가 주인이라서 FK
    private Order order;    //주문

    private int orderPrice; //주문 가격

    private int count;      //주문 수량

}
