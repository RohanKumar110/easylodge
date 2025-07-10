package com.rohankumar.easylodge.services.hotel.impl;

import com.rohankumar.easylodge.dtos.hotel.HotelRequest;
import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.entities.common.ContactInfo;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.exceptions.ResourceNotFoundException;
import com.rohankumar.easylodge.mappers.hotel.HotelMapper;
import com.rohankumar.easylodge.repositories.hotel.HotelRepository;
import com.rohankumar.easylodge.services.hotel.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    @Override
    public HotelResponse createNewHotel(HotelRequest hotelRequest) {

        log.info("Creating the hotel with name: {}", hotelRequest.getName());

        Hotel hotelToSave = HotelMapper.toEntity(hotelRequest);
        hotelToSave.setActive(false);

        Hotel savedHotel = hotelRepository.save(hotelToSave);

        log.info("Hotel created successfully with id: {}", savedHotel.getId());

        return HotelMapper.toResponse(savedHotel);
    }

    @Override
    public HotelResponse getHotelById(UUID id) {

        log.info("Getting the hotel with id: {}", id);

        Hotel fetchedHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        log.info("Hotel fetched successfully with id: {}", fetchedHotel.getId());
        return HotelMapper.toResponse(fetchedHotel);
    }

    @Override
    public boolean updateHotelActivation(UUID id, boolean active) {

        log.info("Updating the hotel with id: {} activation to {}", id, active);

        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        existingHotel.setActive(active);

        hotelRepository.save(existingHotel);

        log.info("Hotel activation updated successfully with id: {}", existingHotel.getId());

        // Todo: Create Inventory for all the rooms for this hotel

        return true;
    }

    @Override
    public HotelResponse updateHotelById(UUID id, HotelRequest hotelRequest) {

        log.info("Updating the hotel with Id: {}", id);

        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        if (hotelRequest.getName() != null) existingHotel.setName(hotelRequest.getName());
        if (hotelRequest.getActive() != null) existingHotel.setActive(hotelRequest.getActive());
        if (hotelRequest.getImages() != null) existingHotel.setImages(hotelRequest.getImages());
        if (hotelRequest.getAmenities() != null) existingHotel.setAmenities(hotelRequest.getAmenities());

        ContactInfo contact = existingHotel.getContactInfo();
        if (contact == null) contact = new ContactInfo();

        if (hotelRequest.getEmail() != null) contact.setEmail(hotelRequest.getEmail());
        if (hotelRequest.getPhone() != null) contact.setPhone(hotelRequest.getPhone());
        if (hotelRequest.getCity() != null) contact.setCity(hotelRequest.getCity());
        if (hotelRequest.getCoordinates() != null) contact.setCoordinates(hotelRequest.getCoordinates());
        if (hotelRequest.getAddress() != null) contact.setAddress(hotelRequest.getAddress());

        existingHotel.setContactInfo(contact);

        Hotel updatedHotel = hotelRepository.save(existingHotel);

        log.info("Hotel updated successfully with Id: {}", updatedHotel.getId());

        return HotelMapper.toResponse(updatedHotel);
    }

    @Override
    public boolean deleteHotelById(UUID id) {

        log.info("Deleting the hotel with id: {}", id);

        boolean exists = hotelRepository.existsById(id);
        if(!exists)
            throw new ResourceNotFoundException("Hotel not found with id: " + id);

        hotelRepository.deleteById(id);

        log.info("Hotel deleted successfully with Id: {}", id);

        // Todo: delete the future inventories for this hotel

        return true;
    }
}
