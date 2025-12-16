package io.github.bagdad.ticketbooking.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingMapper implements RowMapper<Booking> {

    @Override
    public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getLong("id"));
        booking.setFlightId(rs.getLong("flight_id"));
        booking.setPassengerCount(rs.getInt("passenger_count"));
        booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        booking.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return booking;
    }

}
