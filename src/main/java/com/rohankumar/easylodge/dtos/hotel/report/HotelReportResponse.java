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

    public HotelReportResponse(Long bookingCount, BigDecimal totalRevenue, Double avgRevenue) {

        this.bookingCount = bookingCount != null ? bookingCount : 0L;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.avgRevenue = avgRevenue != null ? BigDecimal.valueOf(avgRevenue) : BigDecimal.ZERO;
    }
}
