package jpabook.jpashop.test.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.service.MemberService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;



// 테스트 때 기본 필요 어노테이션들
@RunWith(SpringRunner.class) // junit 에게 스프링 테스트를 알림
@SpringBootTest // 스프링 부트에게 테스트 코드를 알림
@Transactional // 데이터 동작은 트랜잭션 상에서 발생
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long saveId = memberService.join(member);

        // then
        Assertions.assertEquals(member, memberRepository.findOne(saveId));
    }

    // 예외 발생 테스트
    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);
        memberService.join(member2); // 예외가 발생해야 함.

        // then
        Assertions.fail("예외가 발생해야 한다."); // 위에서 문제가 없으면 여기까지 온다.
    }

}
