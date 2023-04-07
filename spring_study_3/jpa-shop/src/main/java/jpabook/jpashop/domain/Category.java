package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // 상위 카테고리 라는개념
    // 자식 입장에선 부모가 하나 - N:1
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    // 부모 입장에선 자식 여럿 - 1:N
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "Category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();
}
