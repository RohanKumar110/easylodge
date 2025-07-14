package com.rohankumar.easylodge.strategies.pricing.impl;

import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.strategies.pricing.PricingStrategy;
import java.math.BigDecimal;

public class BasePricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {

        return inventory.getRoom().getBasePrice();
    }
}
