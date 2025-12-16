package io.github.bagdad.ticketbooking.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("bookings")
@RequiredArgsConstructor
@Getter
@Setter
public class Booking {

    @Id
    private Long id;

    private Long flightId;

    private Integer passengerCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
