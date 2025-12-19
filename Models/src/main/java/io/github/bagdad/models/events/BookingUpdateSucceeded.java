package io.github.bagdad.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingUpdateSucceeded implements IEvent {

    private Long bookingId;

    private Integer passengerCountChange;

}
