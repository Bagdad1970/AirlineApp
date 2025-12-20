package io.github.bagdad.flightmanagement.controller;

import io.github.bagdad.flightmanagement.dto.request.FlightCreate;
import io.github.bagdad.flightmanagement.dto.request.FlightDelete;
import io.github.bagdad.flightmanagement.dto.request.FlightQuery;
import io.github.bagdad.flightmanagement.dto.request.FlightUpdate;
import io.github.bagdad.flightmanagement.model.Flight;
import io.github.bagdad.flightmanagement.service.FlightService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:*")
@RequestMapping("/api/flight")
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

    @PostMapping("/delete")
    public void delete(FlightDelete request) {
        service.deleteById(request.id());
    }

    @GetMapping("/all")
    public List<Flight> findAll() {
        return service.findAll();
    }

    @GetMapping("/export-csv")
    public ResponseEntity<Resource> exportAsCSV() throws IOException {
        Resource resource = service.load();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=flights.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }

    @PostMapping(value="/import-csv", consumes = "application/csv")
    public ResponseEntity<String> importAsCSV(@Parameter(description = "Файл для загрузки", required = true) @RequestParam("file") MultipartFile file) throws IOException {
        service.importFromCSV(file.getInputStream());

        return ResponseEntity.ok("CSV Data Saved into Database");
    }

}
