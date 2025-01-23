package com.chamath.analyticsservice.service;

import com.chamath.analyticsservice.entity.ProductViewCount;
import com.chamath.analyticsservice.event.ProductViewEvent;
import com.chamath.analyticsservice.repositroy.ProductViewRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
//receive messages from the topic and process them to get the view count of products, then updated the db
public class ProductViewEventConsumer {
    public static final  Logger log= LoggerFactory.getLogger(ProductViewEventConsumer.class);

    @Autowired
    private  ReactiveKafkaConsumerTemplate<String, ProductViewEvent> template;

    @Autowired
    private  ProductViewRepository repository;

    @PostConstruct
    public void subscribe(){
        this.template
                .receive()
                .bufferTimeout(1000, Duration.ofSeconds(1)) //receive and passing consumed messages to the downstream to process method. (1000events per second)
                .flatMap(this::process)//after process each batch, return it asynchronously.
                .subscribe();
    }

    //gives the productId and its view count for that batch(1000 message per sec.)
    private Mono<Void> process(List<ReceiverRecord<String,ProductViewEvent>> events){
        var eventsMap= events.stream()
                .map(r-> r.value().getProductId())
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));

        //Counts are updated and new records are created for products not already in the repository
        return this.repository.findAllById(eventsMap.keySet())
                .collectMap(ProductViewCount::getId)
                .defaultIfEmpty(Collections.emptyMap())
                .map(dbMap -> eventsMap.keySet().stream()
                        .map(productId -> updateViewCount(dbMap,eventsMap,productId))
                        .collect(Collectors.toList())
                )
                .flatMapMany(this.repository::saveAll)  //updated record saved back to repo.
                .doOnComplete(()-> events.get(events.size()-1).receiverOffset().acknowledge())
                .doOnError(ex-> log.error(ex.getMessage()))
                .then();
    }


   private ProductViewCount updateViewCount(Map<Integer,ProductViewCount> dbMap, Map<Integer,Long> eventMap, int productId){
       var pvc= dbMap.getOrDefault(productId,new ProductViewCount(productId,0L,true));
       pvc.setCount(pvc.getCount() + eventMap.get(productId));
       return pvc;
   }

      /*
         ex-  -------new events-----
               product 2 - 35 clicks
               product 8 - 2 clicks
               product 11 - 9 clicks

               -------in database-----
               product 2 - 5 clicks
               product 8  - 0 clicks (no previous record in db)
               product 11 - 6 clicks

               ------output-------
               product 2 - 40 clicks
               product 8 - 2 clicks  (new record for p-8)
               product 11- 15 clicks

   */
}
