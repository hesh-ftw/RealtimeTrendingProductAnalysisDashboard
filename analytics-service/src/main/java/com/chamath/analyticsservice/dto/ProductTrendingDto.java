package com.chamath.analyticsservice.dto;
import lombok.Data;


@Data
public class ProductTrendingDto {

    private Integer productId;
    private Long viewCount;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }


    public ProductTrendingDto(Integer productId, Long viewCount) {
        this.productId = productId;
        this.viewCount = viewCount;
    }
}
