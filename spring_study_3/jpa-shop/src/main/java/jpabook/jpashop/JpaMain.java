package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;

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
            Order order = new Order();
            order.addOrderItem(new OrderItem());

            tx.commit();
        }catch (Exception e) {
            tx.rollback();
            System.out.println(e.getMessage());
        }finally {
            em.close();
        }
        emf.close();
    }
}
