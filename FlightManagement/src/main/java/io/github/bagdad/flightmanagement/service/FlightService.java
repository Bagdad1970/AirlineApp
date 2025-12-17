package io.github.bagdad.flightmanagement.service;

import io.github.bagdad.flightmanagement.dto.request.FlightQuery;
import io.github.bagdad.flightmanagement.exception.FlightNotFoundException;
import io.github.bagdad.flightmanagement.helper.CSVHelper;
import io.github.bagdad.flightmanagement.model.Flight;
import io.github.bagdad.flightmanagement.repository.FlightRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository repository;

    FlightService(FlightRepository repository) {
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

        if (flight.getNumber() != null) existing.setFromCity(flight.getNumber());
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
}
