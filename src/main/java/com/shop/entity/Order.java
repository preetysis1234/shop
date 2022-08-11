package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  //many가 order , one이 member
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    //주인 : OrderItem
    //부하 : Order (부모를 따라하기 위해서 cascade는 부하, 즉 자식에게 넣는다)
    //orderitem 즉 아이템이 주인이고 order는 그냥 주문된다는것 자체기 때문에 orderitem이 주인이다.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch =FetchType.LAZY)  //외래키가 아니라 map를 한거임 // 부모를 다 따라해라
    // 고아객체 삭제 >> 리스트객체에 많이 사용된다.
    private List<OrderItem> orderItems = new ArrayList<>(); //양방향 아이템(서로 가지고 있다)
    //order안에 orderitems가 부하가 되는 것이다. 빠진것에 대한 연결된 객체만 삭제된다.

    private LocalDateTime regTime; //주문한 시각

    private LocalDateTime updateTime;

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();  //주문서 객체 생성
        order.setMember(member);    //어떤사람이 시켰는지
        for (OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);  //어떤 아이템을 시켰는지
        }
        order.setOrderStatus(OrderStatus.ORDER);    //현재 상태
        order.setOrderDate(LocalDateTime.now());    //현재 시간 딱
        return order;   //객체를 돌려준다.
    }
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

}
