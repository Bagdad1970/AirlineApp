package io.github.bagdad.flightmanagement.messaging;

import io.github.bagdad.flightmanagement.config.RabbitConfig;
import io.github.bagdad.models.events.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class FlightEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public FlightEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishBookingConfirmed(BookingCreated event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "flight.booking-confirmed",
                new BookingConfirmed(
                        event.getBookingId(),
                        event.getFlightId()
                )
        );
    }

    public void publishBookingRejected(BookingCreated event) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.EXCHANGE,
            "flight.booking-rejected",
            new BookingRejected(
                    event.getBookingId()
            )
        );
    }

    public void publishBookingUpdateSucceeded(BookingUpdated event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "flight.booking-rejected",
                new BookingUpdateSucceeded(
                        event.getBookingId(),
                        event.getPassengerCountChange()
                )
        );
    }

    public void publishBookingUpdateFailed(BookingUpdated event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "flight.booking-rejected",
                new BookingUpdateFailed(
                        event.getBookingId(),
                        event.getPassengerCountChange()
                )
        );
    }

    public void publishFlightCancelled(Long id) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "flight.booking-rejected",
                new FlightCancelled(
                        id
                )
        );
    }
}
