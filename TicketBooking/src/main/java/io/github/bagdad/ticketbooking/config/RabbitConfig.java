package io.github.bagdad.ticketbooking.config;

import io.github.bagdad.models.events.BookingConfirmed;
import io.github.bagdad.models.events.BookingCreated;
import io.github.bagdad.models.events.BookingRejected;
import io.github.bagdad.models.events.FlightCancelled;
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

    public static final String FLIGHT_BOOKING_QUEUE = "flight-booking.queue";
    public static final String BOOKING_FLIGHT_QUEUE = "booking-flight.queue";

    public static final String BOOKING_CREATED_ROUTING_KEY = "booking.created";
    public static final String BOOKING_CONFIRMED_ROUTING_KEY = "booking.confirmed";
    public static final String BOOKING_REJECTED_ROUTING_KEY = "booking.rejected";

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    Queue flightQueue() {
        return new Queue(FLIGHT_BOOKING_QUEUE, true);
    }

    @Bean
    Queue bookingQueue() {
        return new Queue(BOOKING_FLIGHT_QUEUE, true);
    }

    @Bean
    Binding flightBinding() {
        return BindingBuilder
                .bind(flightQueue())
                .to(exchange())
                .with("flight.#");
    }

    @Bean
    Binding bookingBinding() {
        return BindingBuilder
                .bind(bookingQueue())
                .to(exchange())
                .with("booking.#");
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
        idClassMapping.put("booking_created", BookingCreated.class);
        idClassMapping.put("booking_confirmed", BookingConfirmed.class);
        idClassMapping.put("booking_rejected", BookingRejected.class);
        idClassMapping.put("flight_cancelled", FlightCancelled.class);
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
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);

        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return factory;
    }

}