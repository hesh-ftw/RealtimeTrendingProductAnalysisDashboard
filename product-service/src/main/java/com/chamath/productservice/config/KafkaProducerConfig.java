package com.chamath.productservice.config;

import com.chamath.productservice.event.ProductViewEvent;
import com.chamath.productservice.service.ProductViewEventProducer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Sinks;
import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public SenderOptions<String, ProductViewEvent> senderOptions(KafkaProperties properties){
        return SenderOptions.create(properties.buildProducerProperties());
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String,ProductViewEvent> producerTemplate(SenderOptions<String,ProductViewEvent> options){
        return new ReactiveKafkaProducerTemplate<>(options);
    }

    @Bean
    public ProductViewEventProducer productViewEventProducer(ReactiveKafkaProducerTemplate<String,ProductViewEvent> template){

        var sink= Sinks.many().unicast().<ProductViewEvent>onBackpressureBuffer(); //reactive sink that acts as an event queue
        var flux= sink.asFlux();

        var eventProducer= new ProductViewEventProducer(template,sink,flux,"product-view-events");
        eventProducer.subscribe();
        return eventProducer;
    }
}
