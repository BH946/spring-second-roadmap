package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {
        // persistence.xml에 name을 "jpashop" 로 지정했었음.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
            // persistence.xml 에서 create로 설정해뒀기 때문에
            // 엔티티 매핑 테이블 있으면 자동 drop 및 다시 create table을 진행한다.

            // 또한, 테이블 중심으로 엔티티 설계했기 때문에 객체 지향스럽지않다.
            // order->member 로 Order가 Member를 가지게해서 한번에 접근해줘야 하는데,
            // order->memberId->member 형태로 접근하고 있다.
            Order order = em.find(Order.class, 1L); // DB에 있다고 가정
            Long memberId = order.getMemberId();

            Member member = em.find(Member.class, memberId);

            tx.commit();
        }catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
