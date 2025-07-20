package com.rohankumar.easylodge.services.booking.impl;

import com.rohankumar.easylodge.dtos.booking.BookingRequest;
import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.dtos.booking.BookingStatusResponse;
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
import com.rohankumar.easylodge.services.pricing.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    private final PricingService pricingService;
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

        BigDecimal pricePerRoom = pricingService.calculateTotalPrice(inventories);
        BigDecimal totalPrice = pricePerRoom.multiply(BigDecimal.valueOf(bookingRequest.getNumberOfRooms()));

        Booking booking = Booking.builder()
                .status(BookingStatus.RESERVED)
                .amount(totalPrice)
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
    public BookingStatusResponse getBookingStatus(UUID id) {

        return bookingRepository.findById(id)
                .map(booking -> new BookingStatusResponse(booking.getStatus()))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
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

        log.info("Creating Stripe session for booking: {}", id);
        String sessionUrl = paymentService.getSession(paymentRequest);
        log.info("Stripe session created with URL: {}", sessionUrl);

        fetchedBooking.setStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(fetchedBooking);
        log.info("💾 Booking status updated to PAYMENT_PENDING for booking: {}", id);

        log.info("Payment initiation complete for booking: {}", id);

        return new PaymentResponse(sessionUrl);
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {

        if (!"checkout.session.completed".equals(event.getType())) {
            log.info("Unhandled Stripe event type: {}", event.getType());
            return;
        }

        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (session == null) {
            log.info("Failed to deserialize Stripe session object from event: {}", event.getId());
            return;
        }

        String sessionId = session.getId();
        log.info("Processing 'checkout.session.completed' for session: {}", sessionId);

        Booking booking = bookingRepository.findBySessionId(sessionId);

        if (booking == null) {
            log.info("No booking found for session ID: {}", sessionId);
            return;
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {

            log.info("Booking with session {} is already confirmed. Skipping update.", sessionId);
            return;
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        log.info("Booking confirmed with id: {}.", booking.getId());

        Room room = booking.getRoom();
        LocalDate checkIn = booking.getCheckInDate();
        LocalDate checkOut = booking.getCheckOutDate();
        int roomsCount = booking.getNumberOfRooms();

        inventoryRepository.lockBookedOrReservedInventory(room, checkIn, checkOut, roomsCount);
        inventoryRepository.confirmBookingByRoomAndDateBetween(room, checkIn, checkOut, roomsCount);

        log.info("Inventory locked and confirmed for booking: {}", booking.getId());
    }


    @Override
    @Transactional
    public void cancelBooking(UUID id) {
        
        log.info("Initiating cancellation for booking: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {

            log.warn("Attempt to cancel non-confirmed booking: {} with status: {}", id, booking.getStatus());
            throw new BadRequestException("Only confirmed bookings can be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking status updated to CANCELLED with id: {}", id);

        Room room = booking.getRoom();
        LocalDate checkIn = booking.getCheckInDate();
        LocalDate checkOut = booking.getCheckOutDate();
        int roomsCount = booking.getNumberOfRooms();

        inventoryRepository.lockBookedOrReservedInventory(room, checkIn, checkOut, roomsCount);
        inventoryRepository.cancelBookingByRoomAndDateBetween(room, checkIn, checkOut, roomsCount);

        log.info("Inventory updated for cancelled booking: {}", id);

        try {

            Session session = Session.retrieve(booking.getSessionId());

            if (session == null || session.getPaymentIntent() == null) {
                log.warn("Stripe session or payment intent missing for booking: {}", id);
                throw new IllegalStateException("Unable to process refund due to missing payment intent");
            }

            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund refund = Refund.create(refundParams);
            log.info("Refund processed successfully for booking: {} and refund: {}", id, refund.getId());

        } catch (StripeException e) {
            
            log.error("Stripe refund failed for booking: {}", id, e);
            throw new RuntimeException("Failed to process refund via Stripe", e);
        }
    }

    @Override
    public boolean hasBookingExpired(Booking booking) {

        long numberOfMinutes = 10;
        return booking.getCreatedAt().plusMinutes(numberOfMinutes).isBefore(LocalDateTime.now());
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
