package com.chamath.analyticsservice.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductViewEvent {

    private Integer productId;

    // Jackson annotations to ensure deserialization
    @JsonCreator
    public ProductViewEvent(@JsonProperty("productId") Integer productId) {
        this.productId = productId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    //browser
    //location
}
