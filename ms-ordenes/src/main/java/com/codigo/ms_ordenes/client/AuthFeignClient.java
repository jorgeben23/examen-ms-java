package com.codigo.ms_ordenes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-auth", path = "/api/auth")
public interface AuthFeignClient {
    @GetMapping("/validate")
    Map<String, Object> validateToken(@RequestHeader("Authorization") String token);
}


