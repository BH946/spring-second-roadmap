package jpabook.jpashop.web;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

// form태그에 적용할 타입
@Getter @Setter
public class MemberForm {

    // @NotEmpty를 통해서 name필드가 에러면 아래 메시지 보내줌(name의 값이 저게 되는것)
    // @NotEmpty 적용하려면 해당 타입 사용하는 곳에선 @Valid가 필수 선언
    @NotEmpty(message = "회원 이름은 필수 입니다")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
