package com.chamath.productservice.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductViewEvent {

    private Integer productId;


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public ProductViewEvent(Integer productId) {
        this.productId = productId;
    }

    //browser
    //location

}
