package io.github.bagdad.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingCancelled implements IEvent {

    private Long flightId;

    private Integer passengerCount;

}
