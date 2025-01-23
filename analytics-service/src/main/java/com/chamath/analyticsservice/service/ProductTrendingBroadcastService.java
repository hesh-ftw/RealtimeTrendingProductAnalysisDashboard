package com.chamath.analyticsservice.service;

import com.chamath.analyticsservice.dto.ProductTrendingDto;
import com.chamath.analyticsservice.repositroy.ProductViewRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

@Service
//get to 5 trending products
public class ProductTrendingBroadcastService {

    @Autowired
    private ProductViewRepository repository;

    private Flux<List<ProductTrendingDto>> trends;

    @PostConstruct
    public void init(){
        this.trends = this.repository.findTop5ByOrderByCountDesc() //fetch top 5 products based on the view count
                .map(pvc -> new ProductTrendingDto(pvc.getId(), pvc.getCount())) //transform them into a dto
                .collectList()//make a list of data
                .filter(Predicate.not(List::isEmpty)) //ensure only non-empty data is emitted
                .repeatWhen(l-> l.delayElements(Duration.ofSeconds(3)))
                .distinctUntilChanged()
                .cache(1); //avoid recomputing last emitted data
    }

    //return trending products
    public Flux<List<ProductTrendingDto>> getTrends(){
        return trends;
    }


}
