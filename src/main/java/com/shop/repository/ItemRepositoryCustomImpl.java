package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemCategory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.shop.entity.QItemImg;
import org.jboss.jandex.Main;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{  //클래스 재정의

    private JPAQueryFactory queryFactory;   //동적쿼리 사용하기 위해 JPAQueryFactory 변수 선언
    //findby로 만들기 힘들때 동적쿼리를 사용한다.

    //생성자
    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);    //JPAQueryFactory 실질적인 객체가 만들어 집니다.
    }
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ?
                null : QItem.item.itemSellStatus.eq(searchSellStatus);
        //ItemSellStatus null 이면 null 리턴 null 아니면 SELL, SOLD 둘중 하나 리턴
    }

    private BooleanExpression searchCategoryEq(ItemCategory searchCategory){
        return searchCategory == null ?
                null : QItem.item.itemCategory.eq(searchCategory);
    }

    //카테고리에 따른 String값
    private String CategoryEq(ItemCategory itemCategory){
        String Category;
        if(itemCategory.equals(ItemCategory.TOP)){
            Category = "10";
        }else if(itemCategory.equals(ItemCategory.BOTTOM)){
            Category = "20";
        }else if(itemCategory.equals(ItemCategory.DRESS)){
            Category = "30";
        }else if(itemCategory.equals(ItemCategory.ACCESSORY)){
            Category = "40";
        }
        return itemCategory.name();
    }

    private BooleanExpression regDtsAfter(String searchDateType){   //all,1d,1w,1m 6m
        LocalDateTime dateTime = LocalDateTime.now();   //현재시간을 추출해서 변수에 대입

        if(StringUtils.equals("all",searchDateType) || searchDateType == null){
            return null;
        }else if(StringUtils.equals("1d",searchDateType)){
            dateTime = dateTime.minusDays(1);
        }else if(StringUtils.equals("1w",searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        }else if(StringUtils.equals("1m",searchDateType)){
            dateTime = dateTime.minusMonths(1);
        }else if(StringUtils.equals("6m",searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
        //dateTime을 시간에 맞게 세팅 후 시간에 맞는 등록된 상품이 조회하도록 조건값 반환
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("itemNm",searchBy)){  //상품명
            return QItem.item.itemNm.like("%"+searchQuery+"%");
        }else if(StringUtils.equals("createBy",searchBy)){  //작성자
            return QItem.item.createdBy.like("%"+searchQuery+"%");
        }
        return null;
    }



    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        QueryResults<Item> results = queryFactory.selectFrom(QItem.item).
                where(regDtsAfter(itemSearchDto.getSearchDateType()),   //or가 많이 들어가있는 상태
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchCategoryEq(itemSearchDto.getSearchCategory()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults(); //pageble만큼 offset(limit만큼 가져온다)
                            //fetchResult : 리스트랑 값 나옴
        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content,pageable,total);  //결과값이랑 사이즈를 같이 준다.
    }

    //부모쪽 오버라이딩해야한다
    //인터페이스를 재정의 하면 public으로 바뀐다. (프라이빗이더라도)

    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%"+searchQuery+"%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;


        //QMainItemDto @QueryProjection을 하용하면 DTO로 바로 조회 가능
        QueryResults<MainItemDto> results = queryFactory.select(new QMainItemDto(item.id, item.itemNm,
                        item.itemDetail,itemImg.imgUrl,item.price))
                // join 내부조인 .repImgYn.eq("Y") 대표이미지만 가져온다.
                //select 는 list로 봐야한다.
                .from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable,total);
    }


    //추가 항목
    @Override
    public Page<MainItemDto> getCateItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        itemSearchDto.setSearchCategory(ItemCategory.BOTTOM);

        //QMainItemDto @QueryProjection을 하용하면 DTO로 바로 조회 가능
        QueryResults<MainItemDto> results = queryFactory.select(new QMainItemDto(item.id, item.itemNm,
                        item.itemDetail,itemImg.imgUrl,item.price))
                // join 내부조인 .repImgYn.eq("Y") 대표이미지만 가져온다.
                //select 는 list로 봐야한다.
                .from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y"))
                .where(searchCategoryEq(itemSearchDto.getSearchCategory()))
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable,total);
    }



}
