package jpabook.jpashop.repository;


import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor // @Autowired 대신 사용(생성자 이용해서 더 안전성 있는 코드)
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null) { // 상품이 등록되어있나 먼저 확인
            em.persist(item); // save
        }else { // 등록되어 있으면 변동된 상품 정보 update
            em.merge(item); // update
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
