package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {


    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {   //이미지파일을 list로 넣는다.
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage",
                    "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage",
                    "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId")Long itemId, Model model){
        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        }catch (EntityNotFoundException e){
            model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto",new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")        //보통 2로 시작하다 item table check
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }
        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage","상품 수정 중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/";    //다시 실행
   }
   @GetMapping(value = {"/admin/items","/admin/items/{page}"})  //header / page선택
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,Model model){
            //상품관리에서의 검색떄문에 searchdto가 있음
            //모델에 담아서 올릴꺼야
       Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,5); //사이즈가 5 1~5/6~10
       Page<Item> items = itemService.getAdminItemPage(itemSearchDto,pageable); //(getAdminItemPage)검색하고 다시 올라온다. items가 받는다
       model.addAttribute("items",items);
       model.addAttribute("itemSearchDto",itemSearchDto);
       model.addAttribute("maxPage",5); //페이지를 계산할때 필요하다
       return "item/itemMng";
   }



   @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId")Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item",itemFormDto);
        return "item/itemDtl";
   }

    //새로 추가

//    @GetMapping(value = "/item/{itemCategory}")
//    public String itemCate(Model model,@PathVariable)



    /*itemCategory 나누는 부분*/
    @GetMapping(value = "/item/itemBottom")
    public String itemCategoryBottom(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 ,10);
        if(itemSearchDto.getSearchQuery() == null)
        {
            itemSearchDto.setSearchQuery("");
        }
        Page<MainItemDto> items = itemService.getCateItemBottomPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage",5);
        return "item/itemBottom";
    }

    @GetMapping(value = "/item/itemTop")
    public String itemCategoryTop(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 ,10);
        if(itemSearchDto.getSearchQuery() == null)
        {
            itemSearchDto.setSearchQuery("");
        }
        Page<MainItemDto> items = itemService.getCateItemTopPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage",5);
        return "item/itemTop";
    }

    @GetMapping(value = "/item/itemDress")
    public String itemCategoryDress(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 ,10);
        if(itemSearchDto.getSearchQuery() == null)
        {
            itemSearchDto.setSearchQuery("");
        }
        Page<MainItemDto> items = itemService.getCateItemDressPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage",5);
        return "item/itemDress";
    }

    @GetMapping(value = "/item/itemAccessory")
    public String itemCategoryAccessory(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 ,10);
        if(itemSearchDto.getSearchQuery() == null)
        {
            itemSearchDto.setSearchQuery("");
        }
        Page<MainItemDto> items = itemService.getCateItemAccessoryPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage",5);
        return "item/itemAccessory";
    }

    @GetMapping(value = "/item/itemAll")
    public String itemCategoryAll(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 ,10);
        if(itemSearchDto.getSearchQuery() == null)
        {
            itemSearchDto.setSearchQuery("");
        }
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage",5);
        return "item/itemAll";
    }

    @GetMapping(value = "/item/itemSearchResult")
    public String itemSearchResult(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 ,10);
        if(itemSearchDto.getSearchQuery() == null)
        {
            itemSearchDto.setSearchQuery("");
        }
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage",5);
        return "item/itemSearchResult";
    }



}
