package jpabook.jpashop;

import jpabook.jpashop.domain.*;

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
            // Item을 추상 클래스로 만들었으니, Item 하위들로 persist
            // DB 동작 잘 안할땐 전부 drop하고 다시 해보는것도 추천
            Book book = new Book();
            book.setName("JPA");
            book.setAuthor("사람22");

            em.persist(book);

            tx.commit();
        }catch (Exception e) {
            tx.rollback();
            System.out.println(e.getMessage());
            System.out.println("====================================");
        }finally {
            em.close();
        }
        emf.close();
    }
}
