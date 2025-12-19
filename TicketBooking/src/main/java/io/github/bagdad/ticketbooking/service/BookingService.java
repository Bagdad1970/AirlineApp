package io.github.bagdad.ticketbooking.service;

import io.github.bagdad.models.events.*;
import io.github.bagdad.ticketbooking.exception.BookingNotFoundException;
import io.github.bagdad.ticketbooking.helper.CSVHelper;
import io.github.bagdad.ticketbooking.messaging.BookingEventPublisher;
import io.github.bagdad.ticketbooking.model.Booking;
import io.github.bagdad.ticketbooking.repository.BookingRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingEventPublisher publisher;
    private final BookingRepository repository;

    BookingService(BookingEventPublisher publisher, BookingRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    public Booking create(Booking booking) {
        LocalDateTime now = LocalDateTime.now();

        booking.setCreatedAt(now);
        booking.setUpdatedAt(now);

        Booking savedBooking = repository.save(booking);

        publisher.publishBookingCreated(savedBooking);

        return savedBooking;
    }

    public Booking update(Booking booking) {
        Booking existing = repository.findById(booking.getId())
                .orElseThrow(() -> new BookingNotFoundException(booking.getId()));

        if (booking.getPassengerCount() != null) {
            Integer passengerCountChange = existing.getPassengerCount() - booking.getPassengerCount();

            publisher.publishBookingUpdated(existing.getId(), existing.getFlightId(), passengerCountChange);

            existing.setPassengerCount(booking.getPassengerCount());
        }

        existing.setUpdatedAt(LocalDateTime.now());

        return repository.update(existing);
    }

    public List<Booking> findAll() {
        return repository.findAll();
    }

    public void cancel(Long id) {
        Booking existingBooking = repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        publisher.publishBookingCancelled(existingBooking);

        repository.deleteById(id);
    }

    public InputStreamResource load() throws IOException {
        List<Booking> bookings = findAll();

        return new InputStreamResource(
                CSVHelper.writeCsv(bookings)
        );
    }

    public void confirmBooking(BookingConfirmed event) {
        Booking booking = repository.findById(event.getBookingId())
                .orElseThrow();

        LocalDateTime now = LocalDateTime.now();

        booking.setUpdatedAt(now);

        booking.setFlightId(event.getFlightId());

        repository.confirmBooking(booking);
    }

    public void reject(BookingRejected event) {
        if (event.getBookingId() != null) {
            repository.deleteById(event.getBookingId());
        }
    }

    public void cancelBookingsOnFlight(FlightCancelled event) {
        if (event.getFlightId() != null) {
            repository.deleteByFlightId(event.getFlightId());
        }
    }

}
