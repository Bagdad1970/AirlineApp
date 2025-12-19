package io.github.bagdad.flightmanagement.service;

import io.github.bagdad.flightmanagement.dto.request.FlightQuery;
import io.github.bagdad.flightmanagement.exception.FlightNotFoundException;
import io.github.bagdad.flightmanagement.helper.CSVHelper;
import io.github.bagdad.flightmanagement.messaging.FlightEventPublisher;
import io.github.bagdad.flightmanagement.model.Flight;
import io.github.bagdad.flightmanagement.repository.FlightRepository;
import io.github.bagdad.models.events.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightEventPublisher publisher;
    private final FlightRepository repository;

    FlightService(FlightEventPublisher publisher, FlightRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    public Flight create(Flight flight) {
        LocalDateTime now = LocalDateTime.now();

        flight.setCreatedAt(now);
        flight.setUpdatedAt(now);

        return repository.save(flight);
    }

    public Flight update(Flight flight) {
        Flight existing = repository.findById(flight.getId())
                .orElseThrow(() -> new FlightNotFoundException(flight.getId()));

        if (flight.getNumber() != null) existing.setNumber(flight.getNumber());
        if (flight.getFromCity() != null) existing.setFromCity(flight.getFromCity());
        if (flight.getToCity() != null) existing.setToCity(flight.getToCity());
        if (flight.getDeparture() != null) existing.setDeparture(flight.getDeparture());
        if (flight.getArrival() != null) existing.setArrival(flight.getArrival());
        if (flight.getPassengerCount() != null) existing.setPassengerCount(flight.getPassengerCount());
        if (flight.getTicketPrice() != null) existing.setTicketPrice(flight.getTicketPrice());

        existing.setUpdatedAt(LocalDateTime.now());
        return repository.update(existing);
    }

    public List<Flight> findAll() {
        return repository.findAll();
    }

    public List<Flight> query(FlightQuery query) {

        return repository.query(query);
    }

    public void deleteById(Long id) {
        Flight existingFlight = repository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(id));

        publisher.publishFlightCancelled(id);

        repository.deleteById(id);
    }

    public InputStreamResource load() throws IOException {
        List<Flight> bookings = findAll();

        return new InputStreamResource(
                CSVHelper.writeToCSC(bookings)
        );
    }

    public void importFromCSV(InputStream file) throws IOException {
        List<Flight> flights = CSVHelper.loadFromCSV(file);

        if (!flights.isEmpty()) {
            repository.saveAll(flights);
        }
    }

    @Transactional
    public void reserve(BookingCreated event) {
        Flight flight = repository.findById(event.getFlightId())
                .orElseThrow(() -> new FlightNotFoundException(event.getFlightId()));

        if (flight.canBook(event.getPassengerCount())) {
            flight.book(event.getPassengerCount());

            LocalDateTime now = LocalDateTime.now();

            flight.setUpdatedAt(now);

            repository.updatePassengerCount(flight);

            publisher.publishBookingConfirmed(event);
        }
        else {
            publisher.publishBookingRejected(event);
        }
    }

    public void cancelReservation(BookingCancelled event) {
        Flight flight = repository.findById(event.getFlightId())
                .orElseThrow(() -> new FlightNotFoundException(event.getFlightId()));

        flight.cancelReservation(event.getPassengerCount());

        LocalDateTime now = LocalDateTime.now();

        flight.setUpdatedAt(now);

        repository.updatePassengerCount(flight);
    }


    public void updateBookingOnFlight(BookingUpdated event) {
        Flight flight = repository.findById(event.getFlightId())
                .orElseThrow(() -> new FlightNotFoundException(event.getFlightId()));

        if (event.getPassengerCountChange() > 0 || flight.canBook(event.getPassengerCountChange())) {
            flight.book(event.getPassengerCountChange());

            LocalDateTime now = LocalDateTime.now();

            flight.setUpdatedAt(now);

            repository.updatePassengerCount(flight);

            publisher.publishBookingUpdateSucceeded(event);
        }
        else {
            publisher.publishBookingUpdateFailed(event);
        }
    }
}
