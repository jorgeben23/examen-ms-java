package com.codigo.ms_ordenes.client;

import com.codigo.ms_ordenes.aggregates.response.ProductoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "MS-PRODUCTOS", path = "/api/productos")
public interface  PoductosFeignClient {

//    @GetMapping("/listar")
//    List<ProductoResponse> listarProductos(@RequestHeader("Authorization") String token);

    @GetMapping("/{id}")
    ProductoResponse obtenerProductoPorId(@PathVariable("id") Long id,
                                          @RequestHeader("Authorization") String token);
}