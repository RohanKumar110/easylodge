package com.rohankumar.easylodge.strategies.pricing;

import com.rohankumar.easylodge.entities.inventory.Inventory;
import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
