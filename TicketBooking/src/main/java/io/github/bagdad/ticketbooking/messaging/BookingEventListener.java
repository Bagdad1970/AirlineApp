package io.github.bagdad.ticketbooking.messaging;

import com.rabbitmq.client.Channel;
import io.github.bagdad.models.events.*;
import io.github.bagdad.ticketbooking.config.RabbitConfig;
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

    @RabbitListener(queues = RabbitConfig.BOOKING_CONFIRMED_QUEUE)
    public void handleBookingConfirmed(BookingConfirmed event, Message message, Channel channel) throws Exception {
        try {
            service.confirmBooking(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        catch (Exception e) {
            System.err.println("Error processing booking confirmed message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    @RabbitListener(queues = "flight.booking-rejected.queue")
    private void handleBookingRejected(BookingRejected event, Message message, Channel channel) throws Exception {
        try {
            service.reject(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        catch (Exception e) {
            System.err.println("Error processing booking rejected message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    @RabbitListener(queues = "flight.cancelled.queue")
    public void handleFlightCancelled(FlightCancelled event, Message message, Channel channel) throws Exception {
        try {
            service.cancelBookingsOnFlight(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        catch (Exception e) {
            System.err.println("Error processing flight flight cancelled message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    @RabbitListener(queues = RabbitConfig.BOOKING_UPDATE_CONFIRMED_QUEUE)
    public void handleBookingUpdate(BookingUpdateConfirmed event, Message message, Channel channel) throws Exception {
        try {
            service.confirmBookingUpdate(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        catch (Exception e) {
            System.err.println("Error processing flight flight cancelled message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    @RabbitListener(queues = RabbitConfig.BOOKING_UPDATE_REJECTED_QUEUE)
    public void handleFlightCancelled(BookingUpdateRejected event, Message message, Channel channel) throws Exception {
        try {
            System.out.println("Get booking update rejected event: " + event.toString());

            service.rejectBookingUpdate(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        catch (Exception e) {
            System.err.println("Error processing flight flight cancelled message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

}