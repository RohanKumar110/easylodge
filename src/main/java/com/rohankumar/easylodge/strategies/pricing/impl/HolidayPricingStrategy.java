package com.rohankumar.easylodge.strategies.pricing.impl;

import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.strategies.pricing.PricingStrategy;
import com.rohankumar.easylodge.utilities.constants.date.HolidayUtils;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {

        BigDecimal price = wrapped.calculatePrice(inventory);
        boolean isTodayHoliday = HolidayUtils.isPublicHoliday(LocalDate.now());
        if (isTodayHoliday) {
            return price.multiply(BigDecimal.valueOf(1.50));
        }

        return price;
    }
}
