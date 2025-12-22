package io.github.bagdad.flightmanagement.messaging;

import com.rabbitmq.client.Channel;
import io.github.bagdad.flightmanagement.config.RabbitConfig;
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

    @RabbitListener(queues = RabbitConfig.BOOKING_CREATED_QUEUE)
    public void handleBookingCreated(BookingCreated event, Message message, Channel channel) throws Exception {
        try {
            service.reserve(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        catch (Exception e) {
            System.err.println("Error processing flight-booking message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    @RabbitListener(queues = RabbitConfig.BOOKING_UPDATED_QUEUE)
    public void handleBookingUpdated(BookingUpdated event, Message message, Channel channel) throws Exception {
        try {
            service.updateBookingOnFlight(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        catch (Exception e) {
            System.err.println("Error processing flight-booking message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    @RabbitListener(queues = RabbitConfig.BOOKING_CANCELLED_QUEUE)
    public void handleBookingCancelled(BookingCancelled event, Message message, Channel channel) throws Exception {
        try {
            service.cancelReservation(event);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        catch (Exception e) {
            System.err.println("Error processing flight-booking message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

}