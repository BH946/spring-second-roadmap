package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {
        // 아까 name을 "hello" 로 지정했었음.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        // DB변경은 반드시 트랜잭션 내에서! 아래 코드가 정석
        // 물론 현재 스프링은 아래코드보다 간단
        tx.begin();
        try{
            Member member = new Member();
            member.setId(2L);
            member.setName("HelloB");
            em.persist(member);
            tx.commit();
        }catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
