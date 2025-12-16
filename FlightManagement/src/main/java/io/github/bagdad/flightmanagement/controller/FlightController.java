package io.github.bagdad.flightmanagement.controller;

import io.github.bagdad.flightmanagement.dto.request.FlightCreate;
import io.github.bagdad.flightmanagement.dto.request.FlightQuery;
import io.github.bagdad.flightmanagement.dto.request.FlightUpdate;
import io.github.bagdad.flightmanagement.model.Flight;
import io.github.bagdad.flightmanagement.service.FlightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/flight")
@RestController
public class FlightController {

    private final FlightService service;

    public FlightController(FlightService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Flight create(FlightCreate request) {
        Flight flight = new Flight();

        flight.setNumber(request.number());
        flight.setFromCity(request.fromCity());
        flight.setToCity(request.toCity());
        flight.setDeparture(request.departure());
        flight.setArrival(request.arrival());
        flight.setPassengerCount(request.passengerCount());
        flight.setTicketPrice(request.ticketPrice());

        return service.create(flight);
    }

    @PostMapping("/update")
    public Flight update(FlightUpdate request) {
        Flight flight = new Flight();

        flight.setId(request.id());
        flight.setNumber(request.number());
        flight.setFromCity(request.fromCity());
        flight.setToCity(request.toCity());
        flight.setDeparture(request.departure());
        flight.setArrival(request.arrival());
        flight.setPassengerCount(request.passengerCount());
        flight.setTicketPrice(request.ticketPrice());

        return service.update(flight);
    }

    @PostMapping("/query")
    public List<Flight> query(FlightQuery query) {
        return service.query(query);
    }

    @GetMapping("/all")
    public List<Flight> findAll() {
        return service.findAll();
    }

}
