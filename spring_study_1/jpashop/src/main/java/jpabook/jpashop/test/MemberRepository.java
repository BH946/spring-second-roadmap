package jpabook.jpashop.test;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Repository
public class MemberRepository {
    @PersistenceContext
    EntityManager em; // 위 어노테이션을 통해 자동 생성

    public Long save(Member member) {
        em.persist(member); // db 저장
        return member.getId();
    }
    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
