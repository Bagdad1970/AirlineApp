package io.github.bagdad.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingUpdated implements IEvent {

    private Long bookingId;

    private Long flightId;

    private Integer passengerCountChange;

}
