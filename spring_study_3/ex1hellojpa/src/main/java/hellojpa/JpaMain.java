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
        // 물론 현재 스프링 부트는 아래코드보다 간단
        tx.begin();
        try{
            // DB에 id 2인 member 한개 있는상황

            // 영속 엔티티 조회
            System.out.println("start");
            Member findMember = em.find(Member.class, 2L); // 이때 select쿼리
            System.out.println("end");
            // 이때 엔티티 데이터 수정
            findMember.setName("hi"); // 1차캐시에서 더티채킹

            tx.commit(); // 이때 쿼리 날라감. (update쿼리)
        }catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
