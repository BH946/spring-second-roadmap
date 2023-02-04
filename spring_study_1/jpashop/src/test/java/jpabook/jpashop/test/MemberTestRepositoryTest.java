package jpabook.jpashop.test;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberTestRepositoryTest {
    @Autowired
    MemberTestRepository memberRepository;
    @Test
    @Transactional
    @Rollback(false)
    public void testMember() {
        MemberTest member = new MemberTest();
        member.setUsername("memberA");
        Long savedId = memberRepository.save(member);
        MemberTest findMember = memberRepository.find(savedId);
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); // JPA 엔티티 동일성 보장
    }
}