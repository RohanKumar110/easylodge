package com.rohankumar.easylodge.services.guest.impl;

import com.rohankumar.easylodge.dtos.guest.GuestRequest;
import com.rohankumar.easylodge.dtos.guest.GuestResponse;
import com.rohankumar.easylodge.entities.guest.Guest;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.entities.user.User;
import com.rohankumar.easylodge.exceptions.BadRequestException;
import com.rohankumar.easylodge.exceptions.ResourceNotFoundException;
import com.rohankumar.easylodge.mappers.guest.GuestMapper;
import com.rohankumar.easylodge.mappers.room.RoomMapper;
import com.rohankumar.easylodge.repositories.guest.GuestRepository;
import com.rohankumar.easylodge.security.utils.SecurityUtils;
import com.rohankumar.easylodge.services.guest.GuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;

    @Override
    public List<GuestResponse> createNewGuests(List<GuestRequest> guestRequestList) {

        User currentUser = SecurityUtils.getCurrentUser();

        log.info("Creating new guests for user with id: {}", currentUser.getId());

        if(guestRequestList == null || guestRequestList.isEmpty()) {
            log.info("No guests found");
            throw new BadRequestException("Guests are required");
        }

        List<Guest> guestList = guestRequestList.stream()
                .map(guestRequest -> {

            Guest guest = GuestMapper.toEntity(guestRequest);
            guest.setUser(currentUser);
            return guest;

        }).toList();

        List<Guest> guests = guestRepository.saveAll(guestList);
        log.info("Guests saved successfully");
        log.info("Total guests created: {}", guests.size());

        return guestList.stream()
                .map(GuestMapper::toResponse)
                .toList();
    }

    @Override
    public List<GuestResponse> getAllGuests() {

        User currentUser = SecurityUtils.getCurrentUser();
        log.info("Getting all the guests for user with id: {}", currentUser.getId());

        List<Guest> fetchedGuestList = guestRepository.findByUser(currentUser);

        if(fetchedGuestList.isEmpty()) {
            log.info("No guests found for user");
            return List.of();
        }

        log.info("Guests fetched successfully");
        log.info("Total guests found: {}", fetchedGuestList.size());

        return fetchedGuestList.stream()
                .map(GuestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GuestResponse updateGuestById(UUID id, GuestRequest guestRequest) {

        log.info("Updating guest with id: {}", id);

        Guest fetchedGuest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));

        log.info("Guest found successfully with id: {}", id);

        fetchedGuest.setName(guestRequest.getName());
        fetchedGuest.setGender(guestRequest.getGender());
        fetchedGuest.setAge(guestRequest.getAge());

        log.info("Saving the guest");
        Guest savedGuest = guestRepository.save(fetchedGuest);
        log.info("Guest updated successfully with id: {}", id);

        return GuestMapper.toResponse(savedGuest);
    }

    @Override
    public boolean deleteGuestById(UUID id) {

        log.info("Deleting the guest with id: {}", id);

        Guest fetchedGuest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));

        guestRepository.delete(fetchedGuest);

        log.info("Guest deleted successfully with id: {}", id);

        return true;
    }
}
