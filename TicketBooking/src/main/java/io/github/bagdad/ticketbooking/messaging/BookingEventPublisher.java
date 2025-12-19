package io.github.bagdad.ticketbooking.messaging;

import io.github.bagdad.models.events.BookingCancelled;
import io.github.bagdad.models.events.BookingCreated;
import io.github.bagdad.models.events.BookingUpdated;
import io.github.bagdad.models.events.FlightCancelled;
import io.github.bagdad.ticketbooking.config.RabbitConfig;
import io.github.bagdad.ticketbooking.model.Booking;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BookingEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishBookingCreated(Booking booking) {
        BookingCreated event = BookingEventFactory.created(booking);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "booking.created",
                event
        );
    }

    public void publishBookingCancelled(Booking booking) {
        BookingCancelled event = BookingEventFactory.bookingCancelled(booking);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "booking.cancelled",
                event
        );
    }

    public void publishBookingUpdated(Long bookingId, Long flightId, Integer passengerCountChange) {
        BookingUpdated event = BookingEventFactory.bookingUpdated(bookingId, flightId, passengerCountChange);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "booking.updated",
                event
        );
    }

}
