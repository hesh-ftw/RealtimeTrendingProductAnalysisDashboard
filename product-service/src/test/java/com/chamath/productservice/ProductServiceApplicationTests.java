package com.chamath.productservice;

import com.chamath.productservice.event.ProductViewEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@SpringBootTest(classes = ProductServiceApplication.class)
@AutoConfigureWebTestClient
class ProductServiceApplicationTests extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Test
    void productViewEventTest() {

        //user view products
        viewProductSuccess(1);
        viewProductSuccess(1);
        viewProductError(1000);
        viewProductSuccess(5);

        //check if the events are emitted
        var flux= this.<ProductViewEvent> createReceiver(PRODUCT_VIEW_EVENTS)
                .receive().take(3);

        StepVerifier.create(flux)
                .consumeNextWith(r-> Assertions.assertEquals(1,r.value().getProductId()))
                .consumeNextWith(r-> Assertions.assertEquals(1,r.value().getProductId()))
                .consumeNextWith(r-> Assertions.assertEquals(5,r.value().getProductId()))
                .verifyComplete();
    }

    private void viewProductSuccess(Integer id){

        this.client
                .get()
                .uri("/product/"+ id)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                //.consumeWith(response -> System.out.println("Response: " + new String(response.getResponseBody())))
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.description").isEqualTo("product-"+ id);

    }

    private void viewProductError(Integer id){

        this.client
                .get()
                .uri("/product/"+ id)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody();
    }
}
