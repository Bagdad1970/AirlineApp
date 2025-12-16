package io.github.bagdad.ticketbooking.service;

import io.github.bagdad.ticketbooking.exception.BookingNotFoundException;
import io.github.bagdad.ticketbooking.model.Booking;
import io.github.bagdad.ticketbooking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository repository;

    BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public Booking create(Booking booking) {
        LocalDateTime now = LocalDateTime.now();

        booking.setCreatedAt(now);
        booking.setUpdatedAt(now);

        return repository.save(booking);
    }

    public Booking update(Booking booking) {
        Booking existing = repository.findById(booking.getId())
                .orElseThrow(() -> new BookingNotFoundException(booking.getId()));

        if (booking.getPassengerCount() != null) existing.setPassengerCount(booking.getPassengerCount());

        existing.setUpdatedAt(LocalDateTime.now());
        return repository.update(existing);
    }

    public List<Booking> findAll() {
        return repository.findAll();
    }

}
