package com.rohankumar.easylodge.services.guest;

import com.rohankumar.easylodge.dtos.guest.GuestRequest;
import com.rohankumar.easylodge.dtos.guest.GuestResponse;
import java.util.List;
import java.util.UUID;

public interface GuestService {

    List<GuestResponse>  createNewGuests(List<GuestRequest> guestRequestList);

    List<GuestResponse> getAllGuests();

    GuestResponse updateGuestById(UUID id, GuestRequest guestRequest);

    boolean deleteGuestById(UUID id);
}
