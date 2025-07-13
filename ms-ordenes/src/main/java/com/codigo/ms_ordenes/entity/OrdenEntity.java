package com.codigo.ms_ordenes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table (name = "ordenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "orden_productos", joinColumns = @JoinColumn(name = "orden_id"))
    @Column(name = "producto_id")
    private List<Long> productosIds;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "estado")
    @Builder.Default
    private String estado = "PENDIENTE";

    @Column(name = "total")
    private Double total;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
