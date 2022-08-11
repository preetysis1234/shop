package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@ToString
public class Member extends BaseEntity{
    //시간관련 컬럼이 없어도 extends를 통해 자동으로 생긴다.
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    //전화번호 추가
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());  //암호화 >>Bean객체를 많들어서 객체 선언 안해도 됨
        member.setPassword(password);
        member.setPhone(memberFormDto.getPhone());
        member.setRole(Role.ADMIN);
        return member;
    }
}
