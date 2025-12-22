package io.github.bagdad.flightmanagement.messaging;

import io.github.bagdad.flightmanagement.config.RabbitConfig;
import io.github.bagdad.flightmanagement.model.Flight;
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
        BookingConfirmed bookingCreatedEvent = FlightEventFactory.bookingConfirmed(event.getBookingId(), event.getFlightId());

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "flight.booking-confirmed",
                bookingCreatedEvent
        );
    }

    public void publishBookingRejected(BookingCreated event) {
        BookingRejected bookingRejectedEvent = FlightEventFactory.bookingRejected(event.getBookingId());

        rabbitTemplate.convertAndSend(
            RabbitConfig.EXCHANGE,
            "flight.booking-rejected",
                bookingRejectedEvent
        );
    }

    public void publishBookingUpdateConfirmed(BookingUpdated event) {
        BookingUpdateConfirmed bookingUpdatedConfirmedEvent = FlightEventFactory.bookingUpdateConfirmed(
                event.getBookingId(),
                event.getFlightId()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "flight.booking-update-confirmed",
                bookingUpdatedConfirmedEvent

        );
    }

    public void publishBookingUpdateRejected(BookingUpdated event) {
        BookingUpdateRejected bookingUpdateRejectedEvent = FlightEventFactory.bookingUpdateRejected(
                event.getBookingId(),
                event.getFlightId(),
                event.getCurrentPassengerCount(),
                event.getNewPassengerCount()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "flight.booking-update-rejected",
                bookingUpdateRejectedEvent
        );
    }

    public void publishFlightCancelled(Flight flight) {
        FlightCancelled flightCancelledEvent = FlightEventFactory.flightCancelled(flight.getId());

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "flight.cancelled",
                flightCancelledEvent
        );
    }
}
