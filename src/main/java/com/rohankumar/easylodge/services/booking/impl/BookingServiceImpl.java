package com.rohankumar.easylodge.services.booking.impl;

import com.rohankumar.easylodge.dtos.booking.BookingRequest;
import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.dtos.guest.GuestRequest;
import com.rohankumar.easylodge.dtos.guest.GuestResponse;
import com.rohankumar.easylodge.dtos.payment.PaymentRequest;
import com.rohankumar.easylodge.dtos.payment.PaymentResponse;
import com.rohankumar.easylodge.entities.booking.Booking;
import com.rohankumar.easylodge.entities.guest.Guest;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.entities.user.User;
import com.rohankumar.easylodge.enums.booking.BookingStatus;
import com.rohankumar.easylodge.exceptions.BadRequestException;
import com.rohankumar.easylodge.exceptions.ResourceNotFoundException;
import com.rohankumar.easylodge.mappers.booking.BookingMapper;
import com.rohankumar.easylodge.mappers.guest.GuestMapper;
import com.rohankumar.easylodge.repositories.booking.BookingRepository;
import com.rohankumar.easylodge.repositories.guest.GuestRepository;
import com.rohankumar.easylodge.repositories.hotel.HotelRepository;
import com.rohankumar.easylodge.repositories.inventory.InventoryRepository;
import com.rohankumar.easylodge.repositories.room.RoomRepository;
import com.rohankumar.easylodge.security.utils.SecurityUtils;
import com.rohankumar.easylodge.services.booking.BookingService;
import com.rohankumar.easylodge.services.payment.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Value("${app.frontend.url}")
    private String frontendAppUrl;

    private final PaymentService paymentService;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public BookingResponse initializeBooking(BookingRequest bookingRequest) {

        log.info("Initializing booking for hotel [{}] and room [{}]",  bookingRequest.getHotelId(), bookingRequest.getRoomId());

        log.info("Finding hotel with id: {}", bookingRequest.getHotelId());
        Hotel fetchedHotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + bookingRequest.getHotelId()));

        log.info("Finding room with id: {}", bookingRequest.getRoomId());
        Room fetchedRoom = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + bookingRequest.getRoomId()));

        List<Inventory> inventories = inventoryRepository.findAndLockAvailableInventory(
                fetchedRoom, bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getNumberOfRooms());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        if(inventories.size() != daysCount) {
            throw new BadRequestException("Room is not available anymore");
        }

        inventories.forEach(inventory ->
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getNumberOfRooms()));

        inventoryRepository.saveAll(inventories);

        // TODO: calculate dynamic amount

        Booking booking = Booking.builder()
                .status(BookingStatus.RESERVED)
                .amount(BigDecimal.TEN)
                .hotel(fetchedHotel)
                .room(fetchedRoom)
                .user(SecurityUtils.getCurrentUser())
                .numberOfRooms(bookingRequest.getNumberOfRooms())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .build();

        log.info("Creating booking for user: {}", booking.getUser().getId());
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with id: {}", savedBooking.getId());

        return BookingMapper.toResponse(savedBooking);
    }

    @Override
    @Transactional
    public List<GuestResponse> createGuests(UUID id, List<GuestRequest> guests) {

        log.info("Creating guests for booking with id: {}", id);

        Booking fetchedBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if(hasBookingExpired(fetchedBooking)) {
            log.info("Booking has expired");
            throw new BadRequestException("Booking has already expired");
        }

        if(!fetchedBooking.getStatus().equals(BookingStatus.RESERVED)) {
            log.info("Booking is not under reserved state");
            throw new BadRequestException("Booking is not under reserved state, guests cannot be added");
        }

        guests.forEach(guestRequest -> {

            Guest guest = Guest.builder()
                    .name(guestRequest.getName())
                    .gender(guestRequest.getGender())
                    .age(guestRequest.getAge())
                    .booking(fetchedBooking)
                    .user(SecurityUtils.getCurrentUser())
                    .build();

            fetchedBooking.getGuests().add(guest);
        });

        fetchedBooking.setStatus(BookingStatus.GUESTS_ADDED);
        Booking savedBooking = bookingRepository.save(fetchedBooking);
        log.info("Guests created successfully for booking with id: {}", savedBooking.getId());

        return savedBooking.getGuests().stream()
                .map(GuestMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponse initiatePayment(UUID id) {

        log.info("Initiating payment for booking with id: {}", id);

        Booking fetchedBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        User currentUser = SecurityUtils.getCurrentUser();
        if(hasBookingExpired(fetchedBooking)) {
            log.warn("Booking has expired");
            throw new BadRequestException("Booking has already expired");
        }

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBooking(fetchedBooking);
        paymentRequest.setSuccessUrl(frontendAppUrl+"/payment/success");
        paymentRequest.setFailureUrl(frontendAppUrl+"/payment/failure");

        String sessionUrl = paymentService.getSession(paymentRequest);

        fetchedBooking.setStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(fetchedBooking);

        return new PaymentResponse(sessionUrl);
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {

        if("checkout.session.completed".equals(event.getType())) {

            Session session = (Session) event.getDataObjectDeserializer().getObject()
                    .orElse(null);

            if(session != null) {

                Booking fetchedBooking = bookingRepository.findBySessionId(session.getId());
                fetchedBooking.setStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(fetchedBooking);

                inventoryRepository.findAndLockReservedInventory(
                                fetchedBooking.getRoom(), fetchedBooking.getCheckInDate(),
                        fetchedBooking.getCheckOutDate(), fetchedBooking.getNumberOfRooms());

                inventoryRepository.updateBookedAndReservedCountByRoomAndDateBetween(
                        fetchedBooking.getRoom(), fetchedBooking.getCheckInDate(),
                        fetchedBooking.getCheckOutDate(), fetchedBooking.getNumberOfRooms());
            }

        } else {
            log.info("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    public boolean hasBookingExpired(Booking booking) {

        long numberOfMinutes = 10;
        return booking == null;
        //return booking.getCreatedAt().plusMinutes(numberOfMinutes).isBefore(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteGuest(UUID id, UUID guestId) {

        log.info("Deleting guest: {} for booking with id: {}", guestId, id);

        log.info("Finding guest with id: {}", guestId);
        Booking fetchedBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        log.info("Booking found with id: {}", fetchedBooking.getId());

        log.info("Finding guest with id: {}", guestId);
        Guest fetchedGuest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + guestId));

        guestRepository.delete(fetchedGuest);
        log.info("Guest deleted successfully with id: {}", fetchedGuest.getId());
    }
}
