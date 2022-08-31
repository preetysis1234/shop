package com.shop.service;

import com.shop.dto.*;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList)
            throws Exception{
        //상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        //이미지등록
        for (int i =0; i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i==0)    //첫번째
                itemImg.setRepImgYn("Y");
            else
                itemImg.setRepImgYn("N");
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }
        return item.getId();
    }
    @Transactional(readOnly = true) //읽기전용 - > 더티체킹(변경감지)   ->성능향상
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId); //DB에서 데이터를 가지고 옵니다.
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();    //왜 DTO 만들었나요 ???   >>

        for (ItemImg itemimg:itemImgList){  //ItemImg 엔티티를 ItemImgDto 객체를 만들어서 리스트에 추가
            ItemImgDto itemImgDto = ItemImgDto.of(itemimg); //of 연결 DTo로 바꿔주므로써 화면에 나타나게 해준다.
            itemImgDtoList.add(itemImgDto);
        }
        //Item 엔티티를 조회  - > 조회X EntityNotFoundException실행
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item); //item을 dto로 바꿔준다.
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;

    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for (int i =0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true) //불러서 읽기만 하겠다
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto,pageable);
        //itemRepository는 custom의 자식이기 때문에 getMainItemPage가 사용이 가능하다.
    }

    /*itemCategory*/
    @Transactional(readOnly = true)
    public Page<MainItemDto> getCateItemBottomPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getCateItemBottomPage(itemSearchDto,pageable);
        //itemRepository는 custom의 자식이기 때문에 getMainItemPage가 사용이 가능하다.
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getCateItemTopPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getCateItemTopPage(itemSearchDto,pageable);
        //itemRepository는 custom의 자식이기 때문에 getMainItemPage가 사용이 가능하다.
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getCateItemDressPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getCateItemDressPage(itemSearchDto,pageable);
        //itemRepository는 custom의 자식이기 때문에 getMainItemPage가 사용이 가능하다.
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getCateItemAccessoryPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getCateItemAccessoryPage(itemSearchDto,pageable);
        //itemRepository는 custom의 자식이기 때문에 getMainItemPage가 사용이 가능하다.
    }



}
