package com.rohankumar.easylodge.strategies.pricing.impl;

import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.strategies.pricing.PricingStrategy;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {

        BigDecimal price = wrapped.calculatePrice(inventory);

        LocalDate today = LocalDate.now();
        if(!inventory.getInventoryDate().isBefore(today) &&
                inventory.getInventoryDate().isBefore(today.plusDays(7))) {

            return price.multiply(BigDecimal.valueOf(1.25));
        }

        return price;
    }
}
