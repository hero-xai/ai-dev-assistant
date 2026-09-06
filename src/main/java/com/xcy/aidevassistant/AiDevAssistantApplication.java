package com.xcy.aidevassistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.xcy.aidevassistant.project.mapper")
@SpringBootApplication
public class AiDevAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDevAssistantApplication.class, args);
    }
}
