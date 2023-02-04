package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 관례상 order이름 그대로 사용하지말고 바꾸라함
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id") // PK 컬럼명 order_id, id를 식별자로 사용
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 무조건 LAZY 사용할 것
    @JoinColumn(name = "member_id") // FK(외래키)로 사용 - 매핑
    private Member member; // 주문 회원

    // cascade : 영속성 전이
    // 일대다&양방향 => 주인 OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 일대일&양방향 => 양방향에 일대일이므로 주인은 아무나(여기선 Order이 주인)
    // Order에서 Delivery 접근이 일반적이라서 이렇게 설정한 것
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY) // LAZY 필수
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; // 배송 정보

    private LocalDateTime orderDate; // Date보다 이 타입 추천

    // enum 데이터 방식
    @Enumerated(EnumType.STRING) // 타입 꼭 STRING을 추천
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]


    // 연관관계 메서드 => 코드를 줄여줌
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this); // orders 리스트에 추가
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

}
