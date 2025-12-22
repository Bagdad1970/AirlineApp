package io.github.bagdad.ticketbooking.repository;

import io.github.bagdad.models.requests.BookingQueryRequest;
import io.github.bagdad.models.requests.FlightQueryRequest;
import io.github.bagdad.ticketbooking.model.Booking;
import io.github.bagdad.ticketbooking.model.BookingStatistics;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final BookingMapper BOOKING_MAPPER = new BookingMapper();
    private static final StatisticsMapper STATISTICS_MAPPER = new StatisticsMapper();


    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Booking save(Booking booking) {
        String sql = """
        INSERT INTO bookings (
            flight_id, passenger_count, status, created_at, updated_at
        ) VALUES (?, ?, ?, ?, ?)
        RETURNING
            id,
            flight_id,
            passenger_count,
            status,
            created_at,
            updated_at
        """;

        return jdbcTemplate.queryForObject(
                sql,
                BOOKING_MAPPER,
                booking.getFlightId(),
                booking.getPassengerCount(),
                booking.getStatus().name(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }

    public Booking update(Booking booking) {
        String sql = """
            UPDATE bookings SET
                flight_id = ?,
                passenger_count = ?,
                status = ?,
                updated_at = ?
            WHERE id = ?
        """;

        jdbcTemplate.update(
                sql,
                booking.getFlightId(),
                booking.getPassengerCount(),
                booking.getStatus().name(),
                booking.getUpdatedAt(),
                booking.getId()
        );

        return booking;
    }

    public List<Booking> query(BookingQueryRequest query) {
        StringBuilder sql = new StringBuilder("SELECT * FROM bookings WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (query.passengerCount() != null) {
            sql.append(" AND passenger_count = ?");
            params.add(query.passengerCount());
        }

        return jdbcTemplate.query(
                sql.toString(),
                BOOKING_MAPPER,
                params.toArray()
        );
    }

    public Optional<Booking> findById(Long id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";

        try {
            Booking booking = jdbcTemplate.queryForObject(sql, BOOKING_MAPPER, id);
            return Optional.ofNullable(booking);
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Booking> findAll() {
        String sql = "SELECT * FROM bookings";

        return jdbcTemplate.query(
                sql,
                BOOKING_MAPPER
        );
    }

    public void confirmBooking(Booking booking) {
        String sql = """
            UPDATE bookings SET
                flight_id = ?,
                status = ?,
                updated_at = ?
            WHERE id = ?
        """;

        jdbcTemplate.update(
                sql,
                booking.getFlightId(),
                booking.getStatus().name(),
                booking.getUpdatedAt(),
                booking.getId()
        );
    }

    public void deleteById(Long bookingId) {
        String sql = "DELETE FROM bookings WHERE id = ?";

        jdbcTemplate.update(
                sql,
                bookingId
        );
    }

    public void deleteByFlightId(Long flightId) {
        String sql = "DELETE FROM bookings WHERE flight_id = ?";

        jdbcTemplate.update(
                sql,
                flightId
        );
    }

    public void rejectBookingUpdate(Booking booking) {
        String sql = """
            UPDATE bookings SET
                flight_id = ?,
                passenger_count = ?,
                status = ?,
                updated_at = ?
            WHERE id = ?
        """;

        jdbcTemplate.update(
                sql,
                booking.getFlightId(),
                booking.getPassengerCount(),
                booking.getStatus().name(),
                booking.getUpdatedAt(),
                booking.getId()
        );
    }

    public BookingStatistics calculateStatistics() {
        String sql = """
            SELECT
            COUNT(*) as booking_count,
            SUM(passenger_count) as total_passenger_count,
            AVG(passenger_count) as average_passenger_count
            FROM bookings;
        """;

        return jdbcTemplate.queryForObject(
                sql,
                STATISTICS_MAPPER
        );
    }

}
