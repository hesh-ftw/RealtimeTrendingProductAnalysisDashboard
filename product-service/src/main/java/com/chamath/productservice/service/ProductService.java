package com.chamath.productservice.service;

import com.chamath.productservice.dto.ProductDto;
import com.chamath.productservice.event.ProductViewEvent;
import com.chamath.productservice.repository.ProductRepository;
import com.chamath.productservice.util.EntityDtoUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ProductService {

    @Autowired
    private  ProductRepository productRepository;

    @Autowired
    private ProductViewEventProducer productViewEventProducer;

    public Mono<ProductDto> getProduct(int id){
        return productRepository.findById(id)

                //when the product is viewed, this will emit that event to the kafka broker
                .doOnNext(e-> productViewEventProducer.emitEvent(new ProductViewEvent(e.getId())))
                .map(EntityDtoUtil::toDto);
    }

}
