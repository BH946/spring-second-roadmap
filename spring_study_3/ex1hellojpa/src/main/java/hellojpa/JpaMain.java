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
            // DB에 id 2 한개있는 상태
            // 영속
            Member member = em.find(Member.class, 2L); // 1차캐시 비어서 DB접근(select)
            member.setName("AAAA");

//            em.flush(); // 이걸 주석 해제하면 update쿼리 여기서 바로 전송해줌.
            em.detach(member); // 이거 때문에 준영속!! update쿼리 없어짐!!

            System.out.println("-------------");
            tx.commit(); // 준영속이라 이때 아무일도 발생X
        }catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
