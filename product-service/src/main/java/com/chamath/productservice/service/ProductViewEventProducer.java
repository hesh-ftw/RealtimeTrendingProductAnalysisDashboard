package com.chamath.productservice.service;

import com.chamath.productservice.dto.ProductDto;
import com.chamath.productservice.event.ProductViewEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.kafka.sender.SenderRecord;

public class ProductViewEventProducer {

    public ProductViewEventProducer(ReactiveKafkaProducerTemplate<String, ProductViewEvent> producerTemplate, Sinks.Many<ProductViewEvent> sink, Flux<ProductViewEvent> flux, String topic) {
        this.producerTemplate = producerTemplate;
        this.sink = sink;
        this.flux = flux;
        this.topic = topic;
    }

    public static final Logger log= LoggerFactory.getLogger(ProductViewEventProducer.class);

    private final ReactiveKafkaProducerTemplate<String, ProductViewEvent> producerTemplate;
    private final Sinks.Many<ProductViewEvent> sink;
    private final Flux<ProductViewEvent> flux;
    private final String topic;

    public void subscribe(){
        var srFlux = this.flux
                .map(ev-> new ProducerRecord<>(topic,ev.getProductId().toString(),ev))
                .map(pr-> SenderRecord.create(pr,pr.key()));

        this.producerTemplate.send(srFlux)
                .doOnNext(r-> log.info("emitted event : {}",r.correlationMetadata()))
                .subscribe();

    }

    public void emitEvent(ProductViewEvent event){
        sink.tryEmitNext(event);
    }

}
