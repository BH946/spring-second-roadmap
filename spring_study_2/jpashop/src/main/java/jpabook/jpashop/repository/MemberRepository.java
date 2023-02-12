package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// 레퍼지토리는 DB와 제일 연관
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // 만약 엔티티매니저팩토리를 직접 만들어 주입하는 경우 @PersistenceUnit 사용
    // @PersistenceContext 엔티티 매니저 생성하는데 @Autowired가 지원해주므로
    // lombok의 @RequiredArgsConstructor 도 지원해줌
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        // jpql
        // "select m from Member m" => "select * from Member"
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name",
                Member.class)
                .setParameter("name", name) // 쿼리에 변수 삽입 가능
                .getResultList();
    }
}
