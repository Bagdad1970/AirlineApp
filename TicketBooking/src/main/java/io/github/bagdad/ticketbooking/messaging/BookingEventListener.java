package io.github.bagdad.ticketbooking.messaging;

import com.rabbitmq.client.Channel;
import io.github.bagdad.models.events.*;
import io.github.bagdad.ticketbooking.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BookingEventListener {

    private final BookingService service;

    @RabbitListener(queues = "flight-booking.queue")
    public void handleFlightToBookingMessages(IEvent event, Message message, Channel channel) throws Exception {
        try {
            if (event instanceof BookingConfirmed) {
                handleBookingConfirmed((BookingConfirmed) event, message, channel);
            }
            else if (event instanceof BookingRejected) {
                handleBookingRejected((BookingRejected) event, message, channel);
            }
            else if (event instanceof FlightCancelled) {
                handleFlightCancelled((FlightCancelled) event, message, channel);
            }
            else {
                System.err.println("Unknown event type in flight-booking.queue: " + event.getClass());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
        catch (Exception e) {
            System.err.println("Error processing flight-booking message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    private void handleBookingConfirmed(BookingConfirmed event, Message message, Channel channel) throws Exception {
        service.confirmBooking(event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    private void handleBookingRejected(BookingRejected event, Message message, Channel channel) throws Exception {
        service.reject(event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    private void handleFlightCancelled(FlightCancelled event, Message message, Channel channel) throws Exception {
        service.cancelBookingsOnFlight(event);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }



//    @RabbitListener(queues = "flight-booking.queue")
//    public void handleBookingConfirmed(BookingConfirmed event, Message message, Channel channel) throws Exception {
//        try {
//            System.out.println("Received booking created event: " + event);
//
//            service.confirmBooking(event);
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
//
//    @RabbitListener(queues = "booking-flight.queue")
//    public void handleBookingRejected(BookingRejected event, Message message, Channel channel) throws Exception {
//        try {
//            System.out.println("Received booking rejected event: " + event);
//
//            service.rejectBooking(event);
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
//
//    @RabbitListener(queues = "flight.cancelled.queue")
//    public void handleFlightCancelled(FlightCancelled event, Message message, Channel channel) throws Exception {
//        try {
//            System.out.println("Received booking rejected event: " + event);
//
//            service.cancelBookingOnFlight(event);
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

}