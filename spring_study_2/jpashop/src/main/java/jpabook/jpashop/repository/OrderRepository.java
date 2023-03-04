package jpabook.jpashop.repository;



import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;
    public void save(Order order) {
        em.persist(order);
    }
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 검색기능(검색 조건에 동적으로 쿼리를 생성해서 주문 엔티티를 조회한다)
    public List<Order> findAll(OrderSearch orderSearch) {
        // 1) JPQL로 직접 쿼리문들 동적으로 조건문 줘서 다 처리하는 방식
        // => 매우 복잡하게 설계되며, 옛날 방식

        // 2) JPA Criteria 로 동적 쿼리 처리 방식
        // => 훨씬 간편해졌지만, 이또한 옛날 방식

        // 3) Querydsl 방식으로 동적 쿼리 처리 방식
        // => 제일 최신 방식이며 추천!!

        // 그러나 여기선 2번 방법인 JPA Criteria 로 우선 처리하고, 추후에 3번 방식으로 변경하겠다.

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    // 페치조인으로 order -> member , order -> delivery 는이미 조회된 상태이므로 지연로딩X
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .getResultList();
    }

    // fetch join + distinct
    public List<Order> findAllWithItem() {
        return em.createQuery(
                        "select distinct o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d" +
                                " join fetch o.orderItems oi" +
                                " join fetch oi.item i", Order.class)
                .getResultList();
    }

    // ToOne관계는 모두 페치조인한다.
    // 컬렉션 엔티티 조회만 지연 로딩으로 조회한다. 
    // => 하이버네이트 size 설정 사용( 프록시 객체를 size만큼 IN 쿼리로 조회 해줌 )
    // => 당연히 여기서 프록시 객체는 orderitems가 되는것
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset) // 페이징
                .setMaxResults(limit) // 페이징 끝
                .getResultList();
    }
}