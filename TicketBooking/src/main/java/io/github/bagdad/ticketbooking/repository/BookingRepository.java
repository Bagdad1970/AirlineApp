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

//    public void saveAll(List<Booking> bookings) {
//        if (bookings.isEmpty()) {
//            return;
//        }
//
//        String sql = """
//        INSERT INTO bookings (
//            id, flight_id, passenger_count, created_at, updated_at
//        ) VALUES (?, ?, ?, ?, ?)
//    """;
//
//        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                Booking booking = bookings.get(i);
//                ps.setObject(1, booking.getId());
//                ps.setObject(2, booking.getFlightId());
//                ps.setInt(3, booking.getPassengerCount());
//                ps.setTimestamp(4, Timestamp.valueOf(booking.getCreatedAt()));
//                ps.setTimestamp(5, Timestamp.valueOf(booking.getUpdatedAt()));
//            }
//
//            @Override
//            public int getBatchSize() {
//                return bookings.size();
//            }
//        });
//    }

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

    public void confirmBooking(Booking booking) {
        String sql = """
            UPDATE bookings SET
                flight_id = ?,
                updated_at = ?
            WHERE id = ?
        """;

        jdbcTemplate.update(
                sql,
                booking.getFlightId(),
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
}
