package com.shop.dto;


import com.shop.constant.ItemCategory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDto {
    private Long id;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "상세설명은 필수 입력 값입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    private ItemCategory itemCategory;  //카테고리 추가

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();    //상품 이미지 정보 (사진은 하나이상이니까 list)

    private List<Long> itemImgIds = new ArrayList<>();  //상품 이미지 아이디

    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem(){
        return modelMapper.map(this,Item.class);    //ItemFormDto -> Item 연결
    }
    public static ItemFormDto of(Item item){    //바로 메모리에 올라간다.
        return modelMapper.map(item,ItemFormDto.class); //Item -> itemFormDto 연결  //수정과같은 경우에 서로연결하여 사용한다.
    }

}
