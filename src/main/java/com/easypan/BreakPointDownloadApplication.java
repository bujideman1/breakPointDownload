package com.easypan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.easypan"})
@MapperScan("com.easypan.mapper")
public class BreakPointDownloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(BreakPointDownloadApplication.class, args);
    }

}
