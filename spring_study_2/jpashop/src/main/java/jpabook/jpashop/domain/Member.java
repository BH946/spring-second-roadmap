package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id") // 컬럼명 설정 및 이것을 PK로 사용
    private Long id;

    private String name;

    @Embedded // JPA 내장 타입 쓸 때 사용
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member") // 양방향&일대다 => 주인은 member변수
    private List<Order> orders = new ArrayList<>();
}


