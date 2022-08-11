package com.shop.dto;

import com.shop.constant.ItemCategory;
import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    private String searchDateType;

    private ItemSellStatus searchSellStatus;

    private ItemCategory searchCategory;    //카테고리 추가

    private String searchBy;

    private String searchQuery="";
}
