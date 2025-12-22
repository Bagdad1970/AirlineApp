package io.github.bagdad.ticketbooking.config;

import io.github.bagdad.models.events.*;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "booking-exchange";

    public static final String FLIGHT_CANCELLED_QUEUE = "flight.cancelled.queue";
    public static final String BOOKING_CONFIRMED_QUEUE = "flight.booking-confirmed.queue";
    public static final String BOOKING_REJECTED_QUEUE = "flight.booking-rejected.queue";
    public static final String BOOKING_UPDATE_CONFIRMED_QUEUE = "flight.booking-update-confirmed.queue";
    public static final String BOOKING_UPDATE_REJECTED_QUEUE = "flight.booking-update-rejected.queue";

    public static final String BOOKING_CREATED_ROUTING_KEY = "booking.created";
    public static final String BOOKING_CONFIRMED_ROUTING_KEY = "flight.booking-confirmed";
    public static final String BOOKING_REJECTED_ROUTING_KEY = "flight.booking-rejected";

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    Queue flightCancelledQueue() {
        return new Queue(FLIGHT_CANCELLED_QUEUE, true);
    }

    @Bean
    Queue bookingConfirmedQueue() {
        return new Queue(BOOKING_CONFIRMED_QUEUE, true);
    }

    @Bean
    Queue bookingRejectedQueue() {
        return new Queue(BOOKING_REJECTED_QUEUE, true);
    }

    @Bean
    Queue bookingUpdateConfirmedQueue() {
        return new Queue(BOOKING_UPDATE_CONFIRMED_QUEUE, true);
    }

    @Bean
    Queue bookingUpdateRejectedQueue() {
        return new Queue(BOOKING_UPDATE_REJECTED_QUEUE, true);
    }

    @Bean
    Binding flightCancelledBinding() {
        return BindingBuilder
                .bind(flightCancelledQueue())
                .to(exchange())
                .with("flight.cancelled");
    }

    @Bean
    Binding bookingConfirmedBinding() {
        return BindingBuilder
                .bind(bookingConfirmedQueue())
                .to(exchange())
                .with("flight.booking-confirmed");
    }

    @Bean
    Binding bookingRejectedBinding() {
        return BindingBuilder
                .bind(bookingRejectedQueue())
                .to(exchange())
                .with("flight.booking-rejected");
    }

    @Bean
    Binding bookingUpdateConfirmedBinding() {
        return BindingBuilder
                .bind(bookingUpdateConfirmedQueue())
                .to(exchange())
                .with("flight.booking-update-confirmed");
    }

    @Bean
    Binding bookingUpdateRejectedBinding() {
        return BindingBuilder
                .bind(bookingUpdateRejectedQueue())
                .to(exchange())
                .with("flight.booking-update-rejected");
    }



    @Bean
    public MessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setClassMapper(classMapper());
        return converter;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("booking_confirmed", BookingConfirmed.class);
        idClassMapping.put("booking_rejected", BookingRejected.class);
        idClassMapping.put("flight_cancelled", FlightCancelled.class);
        idClassMapping.put("booking_update_rejected", BookingUpdateRejected.class);
        idClassMapping.put("booking_update_confirmed", BookingUpdateConfirmed.class);
        classMapper.setIdClassMapping(idClassMapping);

        return classMapper;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setExchange(EXCHANGE);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);

        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return factory;
    }

}