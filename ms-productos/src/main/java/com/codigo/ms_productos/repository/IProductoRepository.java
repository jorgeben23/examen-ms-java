package com.codigo.ms_productos.repository;

import com.codigo.ms_productos.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductoRepository extends JpaRepository<ProductoEntity, Long> {
}
