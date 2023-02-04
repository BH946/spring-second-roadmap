package jpabook.jpashop.domain.item;
import lombok.Getter;
import lombok.Setter;
import jpabook.jpashop.domain.Category;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


// 이것은 테이블 구조가 상속관계로 구성되어 있음.
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // SINGLE_TABLE 전략 사용위해 선언
@DiscriminatorColumn(name = "dtype") // 부모에는 이 어노테이션 선언
@Getter @Setter
public abstract class Item { // 추상 클래스
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    // 카테고리와 다대다 관계
    // 중요!! 실무에서는 다대다 관계 사용하지 않습니다.
    // 또한 다대다 관계를 관계DB로 나타낼 수 없기 때문에
    // 중간에 매핑 테이블을 하나둬서 연동시켜줍니다.
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();
}