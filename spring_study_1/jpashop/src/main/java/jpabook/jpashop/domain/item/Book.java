package jpabook.jpashop.domain.item;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") // 자식을 표시하는 어노테이션
@Getter @Setter
public class Book extends Item {
    private String author;
    private String isbn;
}