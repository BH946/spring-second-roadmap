package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        // 아까 name을 "hello" 로 지정했었음.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            //검색 - 쿼리의 Member가 테이블이 아닌 "엔티티" 의미
            String jpql = "select m From Member m where m.name like ‘%hello%'";
            List<Member> result = em.createQuery(jpql, Member.class)
                    .getResultList();
            tx.commit(); // 필수

            System.out.println("test");
        }catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
