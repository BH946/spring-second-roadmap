package jpabook.jpashop.service;


import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    /**
     * 영속성 컨텍스트가 자동 변경
     */
//    @Transactional
//    public void updateItem(Long id, String name, int price, int stockQuantity){
//        Item item = itemRepository.findOne(id);
//        item.setName(name);
//        item.setPrice(price);
//        item.setStockQuantity(stockQuantity);
//    }

    // 아래가 권장 코드
    @Transactional
    public void updateItem(Long id, UpdateItemDto itemDto){
        Item item = itemRepository.findOne(id);
        item.change(itemDto.getName(), itemDto.getPrice(), itemDto.getStockQuantity());
    }
}
