package com.Skoglund.Padel.service;

import com.Skoglund.Padel.dto.BookingDTO;
import com.Skoglund.Padel.dto.BookingPatchDTO;
import com.Skoglund.Padel.entity.Booking;
import com.Skoglund.Padel.entity.Court;
import com.Skoglund.Padel.entity.Customer;
import com.Skoglund.Padel.repository.BookingRepository;
import com.Skoglund.Padel.repository.CourtRepository;
import com.Skoglund.Padel.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private static final LocalTime OPEN_TIME = LocalTime.of(7, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(22, 0);

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final CourtRepository courtRepository;
    private final CurrencyService currencyService;

    public BookingService(BookingRepository bookingRepository,
    CustomerRepository customerRepository,
    CourtRepository courtRepository,
    CurrencyService currencyService) {

        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.courtRepository = courtRepository;
        this.currencyService = currencyService;
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));
        return toDTO(booking);
    }

    public List<BookingDTO> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerId(customerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getAvailability(LocalDate date, Long courtId) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new EntityNotFoundException("Court not found with id: " + courtId));

        List<Booking> existingBookings = bookingRepository.findByCourtIdAndDate(courtId, date);
        List<BookingDTO> slots = new ArrayList<>();

        LocalTime current = OPEN_TIME;
        while (current.isBefore(CLOSE_TIME)) {
            LocalTime next = current.plusHours(1);
            final LocalTime slotStart = current;
            final LocalTime slotEnd = next;

            boolean isBooked = existingBookings.stream().anyMatch(b -> b.getStartTime().isBefore(slotEnd) && b.getEndTime().isAfter(slotStart));

            if (!isBooked) {
                BookingDTO slot = new BookingDTO();
                slot.setCourtId(court.getId());
                slot.setCourtName(court.getName());
                slot.setDate(date);
                slot.setStartTime(slotStart);
                slot.setEndTime(slotEnd);
                slots.add(slot);
            }
            current = next;
        }
        return slots;
    }

    @Transactional
    public BookingDTO createBooking(BookingDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + dto.getCustomerId()));
        Court court = courtRepository.findById(dto.getCourtId())
                .orElseThrow(() -> new EntityNotFoundException("Court not found with id: " + dto.getCourtId()));

        validateNoOverlap(dto.getCourtId(), dto.getDate(), dto.getStartTime(), dto.getEndTime(), null);

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setCourt(court);
        booking.setDate(dto.getDate());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setNumberOfPlayers(dto.getNumberOfPlayers());

        BigDecimal priceSek = calculatePrice(court, dto.getStartTime(), dto.getEndTime());
        booking.setTotalPriceSek(priceSek);
        booking.setTotalPriceEur(currencyService.convertSekToEur(priceSek));

        Booking saved = bookingRepository.save(booking);
        logger.info("customer {} created booking {} for court {} on {}",
                customer.getUsername(), saved.getId(), court.getName(), dto.getDate());
        return toDTO(saved);
    }

    @Transactional
    public BookingDTO patchBooking(Long bookingId, BookingPatchDTO patch) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));

        if (patch.getDate() != null) booking.setDate(patch.getDate());
        if (patch.getStartTime() != null) booking.setStartTime(patch.getStartTime());
        if (patch.getEndTime() != null) booking.setEndTime(patch.getEndTime());
        if (patch.getNumberOfPlayers() != null) booking.setNumberOfPlayers(patch.getNumberOfPlayers());
        if (patch.getCourtId() != null) {
            Court court = courtRepository.findById(patch.getCourtId())
                    .orElseThrow(() -> new EntityNotFoundException("Court not found with id: " + patch.getCourtId()));
            booking.setCourt(court);
        }

        validateNoOverlap(booking.getCourt().getId(), booking.getDate(), booking.getStartTime(), booking.getEndTime(), bookingId);

        BigDecimal priceSek = calculatePrice(booking.getCourt(), booking.getStartTime(), booking.getEndTime());
        booking.setTotalPriceSek(priceSek);
        booking.setTotalPriceEur(currencyService.convertSekToEur(priceSek));

        Booking saved = bookingRepository.save(booking);
        logger.info("customer updated booking {} for court {} on {}", saved.getId(), saved.getCourt().getName(), saved.getDate());
        return toDTO(saved);
    }

    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));
        bookingRepository.delete(booking);
        logger.info("admin deleted booking {} (court={}, date={})", id, booking.getCourt().getName(), booking.getDate());
    }

    private void validateNoOverlap(Long courtId, LocalDate date, LocalTime start, LocalTime end, Long excludeBookingId) {
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(courtId, date, start, end);
        overlapping.removeIf(b -> b.getId().equals(excludeBookingId));
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Court is already booked for the requested time slot.");
        }
    }

    private BigDecimal calculatePrice(Court court, LocalTime start, LocalTime end) {
        long hours = ChronoUnit.HOURS.between(start, end);
        return court.getPricePerHourSek().multiply(BigDecimal.valueOf(hours));
    }

    public BookingDTO toDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setCustomerId(booking.getCustomer().getId());
        dto.setCustomerName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName());
        dto.setCourtId(booking.getCourt().getId());
        dto.setCourtName(booking.getCourt().getName());
        dto.setDate(booking.getDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setNumberOfPlayers(booking.getNumberOfPlayers());
        dto.setTotalPriceSek(booking.getTotalPriceSek());
        dto.setTotalPriceEur(booking.getTotalPriceEur());
        return dto;
    }
}