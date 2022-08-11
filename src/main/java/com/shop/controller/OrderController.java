package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody        // 응답에 대한 본체  >> 일단 정보가 바로 올라간다.
    ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult,    //requestbody >> json형식으로 담아논 정보
                         Principal principal){  //principal 현재 로그인 정보를 가지고 있다.
                                                 //값이 여러개니까 dto로 받아서 값을 넣는다.
                                                //json의 키값과 dto의 변수명이 같아야한다.
        if(bindingResult.hasErrors()){  // 문제 발생
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError: fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);   //잘못된 요청 >> sb.toString() >> 주로 404
        }
        String email = principal.getName();
        Long orderId;
        try{
            orderId = orderService.order(orderDto,email);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);  //수량 부족
        }
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,5);

        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(),pageable);

        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page",pageable.getPageNumber());
        model.addAttribute("maxPage",5);
        return "/order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal){
        //값이 하나니까 pathvariable responsebody가 실행된다.
        if(!orderService.validateOrder(orderId,principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }

}
