package jpabook.jpashop.controller;


import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.UpdateItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 등록
     */
    @GetMapping(value = "/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    // DB에 저장하기 위해 form 데이터 BookForm -> Book 타입 변경
    // 여기선 @Vaild 와 @NotEmpty 사용 생략
    @PostMapping(value = "/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        itemService.saveItem(book);
        return "redirect:/items";
    }


    /**
     * 상품 목록(이곳에서도 그냥 엔티티 바로 사용. 실제론 이러면 안됩니다)
     */
    @GetMapping(value = "/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }


    /**
     * 상품 수정(상품 목록에 있는 수정 버튼으로 접근)
     */
    // @PathVariable 로 url에 넘어온 value를 메서드의 파라미터로 가져오게 해줌
    @GetMapping(value = "/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        // 예제니까 그냥 간단히 타입 캐스팅이용
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    // @ModelAttribute 사용시 html의 form 데이터를 매핑해서 담아줌
    @PostMapping(value = "/items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form){
//        Book book = new Book();
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//        itemService.saveItem(book);

        // 아래가 권장 코드
        UpdateItemDto itemDto = new UpdateItemDto(form.getName(), form.getPrice(), form.getStockQuantity());
        itemService.updateItem(itemId, itemDto);
        return "redirect:/items"; // 위에서 "상품 목록" 매핑한 부분으로 이동
    }

}
