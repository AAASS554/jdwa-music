package com.example.jdwa;

import static com.example.jdwa.constant.Constants.ASSETS_PATH;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.jdwa.mapper")
public class JdwaMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdwaMusicApplication.class, args);
    }

}

