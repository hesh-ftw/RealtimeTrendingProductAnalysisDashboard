package com.chamath.analyticsservice.controller;

import com.chamath.analyticsservice.dto.ProductTrendingDto;
import com.chamath.analyticsservice.service.ProductTrendingBroadcastService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.util.List;

@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/trending")
public class TrendingController {

    @Autowired
    private ProductTrendingBroadcastService broadcastService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<ProductTrendingDto>> trendingProducts(){

        return broadcastService.getTrends();
    }

}
