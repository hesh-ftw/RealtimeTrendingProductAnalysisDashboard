package com.chamath.analyticsservice.config;

import com.chamath.analyticsservice.event.ProductViewEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;

@Configuration
public class KafkaConsumerConfig {


    //consume events from the kafka topic configured in product-service
    @Bean
    public ReceiverOptions<String, ProductViewEvent> receiverOptions(KafkaProperties properties){
        return ReceiverOptions
                .<String,ProductViewEvent>create(properties.buildConsumerProperties())
                .consumerProperty(JsonDeserializer.VALUE_DEFAULT_TYPE,ProductViewEvent.class)
                .consumerProperty(JsonDeserializer.USE_TYPE_INFO_HEADERS,false)
                .subscription(List.of("product-view-events"));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String,ProductViewEvent> kafkaConsumerTemplate(ReceiverOptions<String,ProductViewEvent> receiverOptions){
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

}
