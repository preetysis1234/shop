package com.shop.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration  //클래스 설정
@EnableJpaAuditing  //Auditing 기능 활성화   (감시하는 기능)
public class AuditConfig {

    @Bean   //spring container에 등록시키기
    public AuditorAware<String> auditorProvider(){
        return new AuditorAwareImpl();  //빈객체에 등록 AuditorAware<String>(자료형)
    }
}
