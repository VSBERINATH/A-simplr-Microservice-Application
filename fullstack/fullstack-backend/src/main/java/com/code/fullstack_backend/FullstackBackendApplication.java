package com.code.fullstack_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
public class FullstackBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(FullstackBackendApplication.class, args);
    }






    @Bean
    public RestTemplate restTemplate() {
        System.out.println("✅ RestTemplate bean created!");
        return new RestTemplate();

    }
}
