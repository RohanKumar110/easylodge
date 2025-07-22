package com.rohankumar.easylodge.dtos.hotel.report;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelReportResponse {

    private Long bookingCount;
    private BigDecimal totalRevenue;
    private BigDecimal avgRevenue;

    public HotelReportResponse(Long bookingCount, BigDecimal totalRevenue, double avgRevenue) {

        this.bookingCount = bookingCount;
        this.totalRevenue = totalRevenue;
        this.avgRevenue = BigDecimal.valueOf(avgRevenue);
    }
}
