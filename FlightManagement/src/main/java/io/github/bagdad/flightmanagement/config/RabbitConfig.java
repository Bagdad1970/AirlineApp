package io.github.bagdad.flightmanagement.config;

import io.github.bagdad.models.events.*;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
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


@EnableRabbit
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "booking-exchange";

    public static final String BOOKING_CREATED_QUEUE = "booking.created.queue";
    public static final String BOOKING_CANCELLED_QUEUE = "booking.cancelled.queue";
    public static final String BOOKING_UPDATED_QUEUE = "booking.updated.queue";


    public static final String BOOKING_CREATED_ROUTING_KEY = "booking.created";
    public static final String BOOKING_CONFIRMED_ROUTING_KEY = "booking.confirmed";
    public static final String BOOKING_UPDATED_ROUTING_KEY = "booking.updated";
    public static final String BOOKING_REJECTED_ROUTING_KEY = "booking.rejected";
    public static final String FLIGHT_CANCELLED_ROUTING_KEY = "flight.cancelled";

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    Queue bookingCreatedQueue() {
        return new Queue(BOOKING_CREATED_QUEUE, true);
    }

    @Bean
    Queue bookingCancelledQueue() {
        return new Queue(BOOKING_CANCELLED_QUEUE, true);
    }

    @Bean
    Queue bookingUpdatedQueue() {
        return new Queue(BOOKING_UPDATED_QUEUE, true);
    }

    @Bean
    Binding bookingCreatedBinding() {
        return BindingBuilder
                .bind(bookingCreatedQueue())
                .to(exchange())
                .with("booking.created");
    }

    @Bean
    Binding bookingCancelledBinding() {
        return BindingBuilder
                .bind(bookingCancelledQueue())
                .to(exchange())
                .with("booking.cancelled");
    }

    @Bean
    Binding bookingUpdatedBinding() {
        return BindingBuilder
                .bind(bookingUpdatedQueue())
                .to(exchange())
                .with("booking.updated");
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
        idClassMapping.put("booking_cancelled", BookingCancelled.class);
        idClassMapping.put("booking_updated", BookingUpdated.class);
        classMapper.setIdClassMapping(idClassMapping);

        return classMapper;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setExchange(EXCHANGE);
        return template;
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