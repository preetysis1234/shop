package com.shop.entity;

import com.shop.dto.MemberFormDto;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartTest {

    @Autowired
    CartRepository cartRepository;  //쿼리문 실행

    @Autowired
    MemberRepository memberRepository;  //1:1이라서 같이 있어야 실행가능

    @Autowired
    PasswordEncoder passwordEncoder;    //멤버 암호화를 위해서

    @PersistenceContext //영속 컨텍스트
    EntityManager em;

    public Member createMember(){   //member생성
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){
        Member member = createMember();
        memberRepository.save(member);  //멤버 저장

        Cart cart = new Cart();
        cart.setMember(member); //만든 멤버를 cart에 넣어준다.
        cartRepository.save(cart);  //홍길동에 1:1로 매핑

        em.flush(); //영속성 컨텍스트에 데이터를 저장 후 트랜잭션이 끝날 때 flush() 호출하여 데이터베이스에 반영
        em.clear(); //영속성 컨텍스트에 조회 후 엔티티가 없을 경우 데이터 베이스를 조회 영속성 컨텍스트를 비워줍니다.

        Cart savedCart = cartRepository.findById(cart.getId())
                .orElseThrow(EntityNotFoundException::new); //정보가 없을때 실행
        assertEquals(savedCart.getMember().getId(), member.getId());    //있으면 비교
    }
}