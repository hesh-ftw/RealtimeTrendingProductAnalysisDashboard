package com.chamath.analyticsservice;

import com.chamath.analyticsservice.dto.ProductTrendingDto;
import com.chamath.analyticsservice.event.ProductViewEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AutoConfigureWebTestClient
class AnalyticsServiceApplicationTests extends AbstractIntegrationTest{

    public static final Logger log= LoggerFactory.getLogger(AnalyticsServiceApplicationTests.class);

    @Autowired
    private WebTestClient client;

    @Test
    void trendingTest() {

       //emit events
        var events =Flux.just(
                createEvent(2,2),
                createEvent(1,4),
                createEvent(4,8),
                createEvent(5,2),
                createEvent(7,3),
                createEvent(8,6),
                createEvent(9,5),
                createEvent(3,1)
        ).flatMap(Flux::fromIterable)
                .map(e-> this.toSenderRecord(PRODUCT_VIEW_EVENTS, e.getProductId().toString(),e));

        var resultFlux = this.<ProductViewEvent>createSender().send(events);
        StepVerifier.create(resultFlux)
                .expectNextCount(31)
                .verifyComplete();

        // verify via trending endpoint
        var mono = this.client
                .get()
                .uri("/trending")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .returnResult(new ParameterizedTypeReference<List<ProductTrendingDto>>(){})
                .getResponseBody()
                .next();

        StepVerifier.create(mono)
                .consumeNextWith(this::validateResult)
                .verifyComplete();
    }


    //product-4,8,9,1,7  -exclude 2

    private void validateResult(List<ProductTrendingDto> list){
        list.forEach(p -> log.info("Product ID: {}, View Count: {}", p.getProductId(), p.getViewCount()));
        Assertions.assertEquals(5,list.size());
        Assertions.assertEquals(4,list.get(0).getProductId());
        Assertions.assertEquals(8,list.get(0).getViewCount());
        Assertions.assertEquals(7,list.get(4).getProductId());
        Assertions.assertEquals(3,list.get(4).getViewCount());
        Assertions.assertTrue(list.stream().noneMatch(p->p.getProductId()==2));
    }

    private List<ProductViewEvent> createEvent(int productId, int count){
        return IntStream.rangeClosed(1,count)
                .mapToObj(i-> new ProductViewEvent(productId))
                .collect(Collectors.toList());
    }

}
