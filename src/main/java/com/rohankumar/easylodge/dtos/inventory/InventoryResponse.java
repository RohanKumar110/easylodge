package com.rohankumar.easylodge.dtos.inventory;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private Long id;
    private LocalDate inventoryDate;
    private Integer bookedCount;
    private Integer reservedCount;
    private Integer totalRoomsCount;
    private BigDecimal surgeFactor;
    private BigDecimal price;
    private Boolean closed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
