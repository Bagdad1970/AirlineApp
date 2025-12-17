package io.github.bagdad.ticketbooking.controller;

import io.github.bagdad.ticketbooking.dto.request.BookingCreate;
import io.github.bagdad.ticketbooking.dto.request.BookingUpdate;
import io.github.bagdad.ticketbooking.model.Booking;
import io.github.bagdad.ticketbooking.service.BookingService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/booking")
@RestController
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Booking create(BookingCreate request) {
        Booking booking = new Booking();

        booking.setFlightId(request.flightId());
        booking.setPassengerCount(request.passengerCount());

        return service.create(booking);
    }

    @PostMapping("/update")
    public Booking update(BookingUpdate request) {
        Booking booking = new Booking();

        booking.setId(request.id());
        booking.setPassengerCount(request.passengerCount());

        return service.update(booking);
    }

    @GetMapping("/all")
    public List<Booking> findAll() {
        return service.findAll();
    }

    @GetMapping("/export-csv")
    public ResponseEntity<Resource> exportToCSV() throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=bookings.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(service.load());
    }

}
