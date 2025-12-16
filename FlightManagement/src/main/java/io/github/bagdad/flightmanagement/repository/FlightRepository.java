package io.github.bagdad.flightmanagement.repository;

import io.github.bagdad.flightmanagement.dto.request.FlightQuery;
import io.github.bagdad.flightmanagement.model.Flight;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FlightRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final FlightMapper mapper = new FlightMapper();

    public FlightRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Flight save(Flight flight) {
        String sql = """
        INSERT INTO flights (
            number, from_city, to_city, departure, arrival, passenger_count, ticket_price, created_at, updated_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING
            id,
            number,
            from_city,
            to_city,
            departure,
            arrival,
            passenger_count,
            ticket_price,
            created_at,
            updated_at
        """;

        return jdbcTemplate.queryForObject(
                sql,
                mapper,
                flight.getNumber(),
                flight.getFromCity(),
                flight.getToCity(),
                flight.getDeparture(),
                flight.getArrival(),
                flight.getPassengerCount(),
                flight.getTicketPrice(),
                flight.getCreatedAt(),
                flight.getUpdatedAt()
        );
    }

    public Flight update(Flight flight) {
        String sql = """
            UPDATE flights SET
                number = ?,
                from_city = ?,
                to_city  = ?,
                departure = ?,
                arrival = ?,
                passenger_count = ?,
                ticket_price = ?,
                updated_at = ?
            WHERE id = ?
        """;

        jdbcTemplate.update(
                sql,
                flight.getNumber(),
                flight.getFromCity(),
                flight.getToCity(),
                flight.getDeparture(),
                flight.getArrival(),
                flight.getPassengerCount(),
                flight.getTicketPrice(),
                flight.getUpdatedAt(),
                flight.getId()
        );

        return flight;
    }

    public List<Flight> query(FlightQuery query) {
        StringBuilder sql = new StringBuilder("SELECT * FROM flights WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (query.number() != null && !query.number().isBlank()) {
            sql.append(" AND number = ?");
            params.add(query.number());
        }

        if (query.fromCity() != null && !query.fromCity().isBlank()) {
            sql.append(" AND from_city = ?");
            params.add(query.fromCity());
        }

        if (query.toCity() != null && !query.toCity().isBlank()) {
            sql.append(" AND to_city = ?");
            params.add(query.toCity());
        }

        if (query.departureMin() != null) {
            sql.append(" AND departure >= ?");
            params.add(query.departureMin());
        }

        if (query.departureMax() != null) {
            sql.append(" AND departure <= ?");
            params.add(query.departureMax());
        }

        if (query.arrivalMin() != null) {
            sql.append(" AND arrival >= ?");
            params.add(query.arrivalMin());
        }

        if (query.arrivalMax() != null) {
            sql.append(" AND arrival <= ?");
            params.add(query.arrivalMax());
        }

        if (query.passengerCountMin() != null) {
            sql.append(" AND passenger_count >= ?");
            params.add(query.passengerCountMin());
        }

        if (query.passengerCountMax() != null) {
            sql.append(" AND passenger_count <= ?");
            params.add(query.passengerCountMax());
        }

        if (query.ticketPriceMin() != null) {
            sql.append(" AND ticket_price >= ?");
            params.add(query.ticketPriceMin());
        }

        if (query.ticketPriceMax() != null) {
            sql.append(" AND ticket_price <= ?");
            params.add(query.ticketPriceMax());
        }

        return jdbcTemplate.query(
                sql.toString(),
                mapper,
                params.toArray()
        );
    }

    public Optional<Flight> findById(Long id) {
        String sql = "SELECT * FROM flights WHERE id = ?";

        try {
            Flight flight = jdbcTemplate.queryForObject(sql, mapper, id);
            return Optional.ofNullable(flight);
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Flight> findAll() {
        String sql = "SELECT * FROM flights";

        return jdbcTemplate.query(
            sql,
            mapper
        );
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM flights WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }


}
