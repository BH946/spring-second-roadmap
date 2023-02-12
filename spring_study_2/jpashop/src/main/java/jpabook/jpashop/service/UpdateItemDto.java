package jpabook.jpashop.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor // 생성자
public class UpdateItemDto {
    private final String name;
    private final int price;
    private final int stockQuantity;

//    UpdateItemDto(String name, int price, int stockQuantity) {
//        this.name = name;
//        this.price = price;
//        this.stockQuantity = stockQuantity;
//    }
}
