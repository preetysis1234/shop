package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")   //외래키
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")  //외래키가 여기 있기 때문에 order를 대상으로 주인이다.외래키로써 쓰이는 곳이 부하, 외래키가 존재하는 곳이 주인
    private Order order;

    private int orderPrice;

    private int count;
    /*

     private LocalDateTime regTime;

     private LocalDateTime updateTime;

    extends때문에 위에 변수는 자동선언이 되기때문에 지워도 된다.
     */

    public static OrderItem createOrderItem(Item item, int count){  //static이다
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());
        item.removeStock(count);    //주문한 수량만큼 빠져야한다.
        return  orderItem;
    }
    public int getTotalPrice(){

        return  orderPrice*count;
    }

    public void cancel(){
        this.getItem().addStock(count); //넘어온 수량을 다시 채운다.
    }


}
