package com.codigo.ms_productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsProductosApplication {

    public static void main (String[] args) {
        SpringApplication.run(MsProductosApplication.class, args);
    }
}
