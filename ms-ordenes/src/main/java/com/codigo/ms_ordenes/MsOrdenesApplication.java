package com.codigo.ms_ordenes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsOrdenesApplication {

    public static void main (String[] args) {
        SpringApplication.run(MsOrdenesApplication.class, args);
    }
}
