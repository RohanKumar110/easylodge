package com.rohankumar.easylodge.strategies.pricing.impl;

import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.strategies.pricing.PricingStrategy;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class SurgePriceStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {

        BigDecimal price = wrapped.calculatePrice(inventory);
        return price.multiply(inventory.getSurgeFactor());
    }
}