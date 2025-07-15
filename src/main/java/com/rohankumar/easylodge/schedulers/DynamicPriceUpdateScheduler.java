package com.rohankumar.easylodge.schedulers;

import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.hotel.HotelDailyPrice;
import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.repositories.hotel.HotelDailyPriceRepository;
import com.rohankumar.easylodge.repositories.hotel.HotelRepository;
import com.rohankumar.easylodge.repositories.inventory.InventoryRepository;
import com.rohankumar.easylodge.services.pricing.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DynamicPriceUpdateScheduler {

    // Scheduler to update the inventory and hotelDailyPrice tables every hour

    private final PricingService pricingService;
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelDailyPriceRepository dailyPriceRepo;

    @Scheduled(cron = "0 0 * * * *")
    public void runHourlyUpdate() {
        log.info("Starting hourly dynamic price update...");

        int pageNo = 0;
        int pageSize = 100;

        while (true) {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<Hotel> hotelPage = hotelRepository.findAll(pageable);

            if (hotelPage.isEmpty()) {
                log.info("No more hotels found to process. Ending update after {} pages.", pageNo);
                break;
            }

            log.info("Processing page {} with {} hotels.", pageNo, hotelPage.getNumberOfElements());

            hotelPage.getContent().forEach(hotel -> {
                log.info("Refreshing prices for hotel with ID: {}", hotel.getId());
                refreshPricesForHotel(hotel);
            });

            pageNo++;
        }

        log.info("Completed hourly dynamic price update.");
    }

    private void refreshPricesForHotel(Hotel hotel) {

        log.info("Calculating prices for hotel with ID: {}", hotel.getId());

        LocalDate today          = LocalDate.now();
        LocalDate oneYearOut     = today.plusYears(1);
        LocalDate ninetyDaysOut  = today.plusDays(89);

        List<Inventory> yearlyInventories = inventoryRepository.findByHotelAndDateBetween(hotel, today, oneYearOut);
        log.info("Found {} inventory records for hotel ID: {} between {} and {}.", yearlyInventories.size(), hotel.getId(), today, oneYearOut);

        updateInventoryPrices(yearlyInventories);

        List<Inventory> ninetyDayInventories = inventoryRepository.findByHotelAndDateBetween(hotel, today, ninetyDaysOut);
        updateHotelDailyPrice(hotel, ninetyDayInventories);

        log.info("Completed price calculation for hotel ID: {}", hotel.getId());
    }

    private void updateInventoryPrices(List<Inventory> inventories) {

        log.info("Updating prices for {} inventory records.", inventories.size());

        inventories.forEach(inventory -> {
            BigDecimal price = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(price);
            log.debug("Updated inventory ID: {} with price: {}", inventory.getId(), price);
        });

        inventoryRepository.saveAll(inventories);
        log.info("Saved updated inventory prices.");
    }

    private void updateHotelDailyPrice(Hotel hotel, List<Inventory> inventories) {

        log.info("Updating HotelDailyPrice for hotel ID: {}", hotel.getId());

        Map<LocalDate, BigDecimal> dailyMinPrices = inventories.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getInventoryDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().orElse(BigDecimal.ZERO)
                ));

        log.info("Calculated minimum prices for {} dates.", dailyMinPrices.size());

        List<HotelDailyPrice> hotelDailyPriceList = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelDailyPrice hotelDailyPrice = dailyPriceRepo.findByHotelAndDate(hotel, date)
                    .orElse(new HotelDailyPrice(hotel, date));
            hotelDailyPrice.setPrice(price);
            hotelDailyPriceList.add(hotelDailyPrice);
            log.debug("Prepared HotelDailyPrice for date: {} with price: {}", date, price);
        });

        dailyPriceRepo.saveAll(hotelDailyPriceList);
        log.info("Saved {} HotelDailyPrice records for hotel ID: {}", hotelDailyPriceList.size(), hotel.getId());
    }
}
