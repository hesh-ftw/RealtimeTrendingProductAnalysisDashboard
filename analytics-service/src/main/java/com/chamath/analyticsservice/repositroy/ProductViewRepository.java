package com.chamath.analyticsservice.repositroy;

import com.chamath.analyticsservice.entity.ProductViewCount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductViewRepository extends ReactiveCrudRepository<ProductViewCount,Integer> {

    //get the top 5 trending products based on the order clicked count as a flux
    Flux<ProductViewCount> findTop5ByOrderByCountDesc();
}
