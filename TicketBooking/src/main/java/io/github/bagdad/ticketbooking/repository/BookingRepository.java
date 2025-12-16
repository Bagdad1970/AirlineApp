package io.github.bagdad.ticketbooking.repository;

import io.github.bagdad.ticketbooking.model.Booking;
import io.github.bagdad.ticketbooking.model.BookingMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final BookingMapper mapper = new BookingMapper();

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Booking save(Booking booking) {
        String sql = """
        INSERT INTO bookings (
            flight_id, passenger_count, created_at, updated_at
        ) VALUES (?, ?, ?, ?)
        RETURNING
            id,
            flight_id,
            passenger_count,
            created_at,
            updated_at
        """;

        return jdbcTemplate.queryForObject(
                sql,
                mapper,
                booking.getFlightId(),
                booking.getPassengerCount(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }

    public Booking update(Booking booking) {
        String sql = """
            UPDATE bookings SET
                flight_id = ?,
                passenger_count = ?,
                updated_at = ?
            WHERE id = ?
        """;

        jdbcTemplate.update(
                sql,
                booking.getFlightId(),
                booking.getPassengerCount(),
                booking.getUpdatedAt(),
                booking.getId()
        );

        return booking;
    }

    public Optional<Booking> findById(Long id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";

        try {
            Booking booking = jdbcTemplate.queryForObject(sql, mapper, id);
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
                mapper
        );
    }

}
