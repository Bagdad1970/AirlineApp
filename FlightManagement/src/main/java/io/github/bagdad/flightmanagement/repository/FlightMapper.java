package io.github.bagdad.flightmanagement.repository;

import io.github.bagdad.flightmanagement.model.Flight;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class FlightMapper implements RowMapper<Flight> {

    @Override
    public Flight mapRow(ResultSet rs, int rowNum) throws SQLException {
        Flight flight = new Flight();
        flight.setId(rs.getLong("id"));
        flight.setNumber(rs.getString("number"));
        flight.setFromCity(rs.getString("from_city"));
        flight.setToCity(rs.getString("to_city"));
        flight.setDeparture(rs.getObject("departure", OffsetDateTime.class));
        flight.setArrival(rs.getObject("arrival", OffsetDateTime.class));
        flight.setPassengerCount(rs.getInt("passenger_count"));
        flight.setTicketPrice(rs.getBigDecimal("ticket_price"));
        flight.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        flight.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return flight;
    }

}
