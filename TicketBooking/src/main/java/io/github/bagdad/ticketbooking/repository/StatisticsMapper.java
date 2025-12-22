package io.github.bagdad.ticketbooking.repository;

import io.github.bagdad.ticketbooking.model.BookingStatistics;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticsMapper implements RowMapper<BookingStatistics> {

    @Override
    public BookingStatistics mapRow(ResultSet rs, int rowNum) throws SQLException {
        BookingStatistics bookingStatistics = new BookingStatistics();
        bookingStatistics.setBookingCount(rs.getInt("booking_count"));
        bookingStatistics.setTotalPassengerCount(rs.getInt("total_passenger_count"));
        bookingStatistics.setAveragePassengerCount(rs.getFloat("average_passenger_count"));

        return bookingStatistics;
    }

}