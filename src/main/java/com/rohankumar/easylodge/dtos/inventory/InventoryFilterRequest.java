package com.rohankumar.easylodge.dtos.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rohankumar.easylodge.utils.constants.AppConstants;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryFilterRequest {

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer pageNo = AppConstants.PAGE_NO;
    private Integer pageSize = AppConstants.PAGE_SIZE;
}
