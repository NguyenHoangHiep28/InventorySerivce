package com.example.inventoryservice.entity;

import com.example.inventoryservice.entity.base.BaseEntity;
import com.example.inventoryservice.entity.enums.IEHistoryType;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "iehistories")
@Builder
public class IEHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.ORDINAL)
    private IEHistoryType type;
    private Integer quantity;
    private Integer currentProductQty;
    private Boolean success;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private String orderId;
}
