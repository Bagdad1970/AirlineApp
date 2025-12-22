package io.github.bagdad.ticketbooking.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingStatistics {

    private Float averagePassengerCount;

    private Integer totalPassengerCount;

    private Integer bookingCount;

}
