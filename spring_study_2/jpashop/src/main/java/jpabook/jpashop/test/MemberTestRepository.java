package jpabook.jpashop.test;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Repository
public class MemberTestRepository {
    @PersistenceContext
    EntityManager em; // 위 어노테이션을 통해 자동 생성

    public Long save(MemberTest member) {
        em.persist(member); // db 저장
        return member.getId();
    }
    public MemberTest find(Long id) {
        return em.find(MemberTest.class, id);
    }
}
