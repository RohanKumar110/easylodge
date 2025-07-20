package com.rohankumar.easylodge.services.pricing;

import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.strategies.pricing.PricingStrategy;
import com.rohankumar.easylodge.strategies.pricing.impl.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory) {

        PricingStrategy pricingStrategy = new BasePricingStrategy();
        pricingStrategy = new SurgePriceStrategy(pricingStrategy);
        pricingStrategy = new OccupancyStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }

    public BigDecimal calculateTotalPrice(List<Inventory> inventories) {

        return inventories.stream()
                .map(this::calculateDynamicPricing)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
