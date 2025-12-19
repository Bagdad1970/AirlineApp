package io.github.bagdad.flightmanagement.messaging;

import com.rabbitmq.client.Channel;
import io.github.bagdad.flightmanagement.service.FlightService;
import io.github.bagdad.models.events.*;
import lombok.AllArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FlightEventListener {

    private final FlightService service;

//    @RabbitListener(queues = RabbitConfig.BOOKING_FLIGHT_QUEUE)
//    public void handleEvent(BookingCreated event, Message message, Channel channel) throws Exception {
//        try {
//            System.out.println("Received booking created event: " + event);
//
//            service.reserve(event);
//
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//        }
//        catch (Exception e) {
//            System.err.println("Error processing message: " + e.getMessage());
//
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
//
//            throw new AmqpRejectAndDontRequeueException(e);
//        }
//    }

    @RabbitListener(queues = "booking-flight.queue")
    public void handleFlightToBookingMessages(IEvent event, Message message, Channel channel) throws Exception {
        try {
            if (event instanceof BookingCreated) {
                handleBookingCreated((BookingCreated) event, message, channel);
            }
            else if (event instanceof BookingCancelled) {
                handleBookingCancelled((BookingCancelled) event, message, channel);
            }
            else if (event instanceof BookingUpdated) {
                handleBookingUpdated((BookingUpdated) event, message, channel);
            }
            else {
                System.err.println("Unknown event type in booking-flight.queue: " + event.getClass());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
        catch (Exception e) {
            System.err.println("Error processing flight-booking message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    private void handleBookingCreated(BookingCreated event, Message message, Channel channel) throws Exception {
        service.reserve(event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    private void handleBookingCancelled(BookingCancelled event, Message message, Channel channel) throws Exception {
        service.cancelReservation(event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    private void handleBookingUpdated(BookingUpdated event, Message message, Channel channel) throws Exception {
        service.updateBookingOnFlight(event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

//    private void handleBookingUpdated(BookingUpdated event, Message message, Channel channel) throws Exception {
//        service.updateBookingOnFlight(event);
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//    }

}