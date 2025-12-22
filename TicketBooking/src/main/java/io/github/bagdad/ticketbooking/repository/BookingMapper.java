package io.github.bagdad.ticketbooking.repository;

import io.github.bagdad.ticketbooking.model.Booking;
import io.github.bagdad.ticketbooking.model.BookingStatus;
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
        String statusStr = rs.getString("status");
        booking.setStatus(statusStr != null
                ? BookingStatus.valueOf(statusStr.toUpperCase())
                : null);
        booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        booking.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return booking;
    }

}
