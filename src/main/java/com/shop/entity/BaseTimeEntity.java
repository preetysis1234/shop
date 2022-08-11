package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(value = {AuditingEntityListener.class})    //Auditing을 하기위해 엔티티 리스너 추가
@MappedSuperclass   //부모 클래스를 상속 받는 자식 클래스에 매핑정보만 제공
@Getter
@Setter
public abstract class BaseTimeEntity {

    @CreatedDate    //생성시 자동 저장
    @Column(updatable = false)  //수정불가
    private LocalDateTime regTime;

    @LastModifiedDate   //변경시 자동 저장
    private LocalDateTime updateTime;
}
