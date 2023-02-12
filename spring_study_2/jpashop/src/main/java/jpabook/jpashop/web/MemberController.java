package jpabook.jpashop.web;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor // @Autowired 대신 및 더 안전성 있는 생산자 방식
public class MemberController {

    private final MemberService memberService;


    /**
     * 회원 등록(가입) 부분
     */
    @GetMapping(value = "/members/new")
    public String createForm(Model model) {
        // 매번 언급했지만 Model타입은 html에 데이터 담아서 보내는 용도
        // new MemberFrom()으로 빈 객체라도 보냈기 때문에 form태그에서 사용 및 POST에도 해당 타입으로 응답가능한거
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    // form태그에서 action에 "/members/new" 로 POST 형태로 요청옴
    // @Valid를 통해서 @NotEmpty 확인
    // BindingResult를 통해서 에러도 감지
    @PostMapping(value = "/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        if(result.hasErrors()) return "members/createMemberForm";

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member); // 회원가입 (이때 merge를 통해서 준속성 엔티티 -> 영속성 엔티티)
        return "redirect:/"; // 기본 home 페이지로 돌아감
    }

    
    /**
     * 회원 조회 부분(GET이면 충분)
     */
    // 참고로 그냥 Member 엔티티 사용했는데 실무에선 위에 MemberForm 타입처럼
    // 웹에선 웹 전용으로 따로 만들어서 꼭 사용할것!!(간단히 생각해도 이게 유지보수 좋음)
    // 현재는 그냥 예제니까 그냥 엔티티 바로 가져다 사용한것일 뿐
    @GetMapping(value = "/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }

}
