package com.chamath.productservice.util;

import com.chamath.productservice.dto.ProductDto;
import com.chamath.productservice.entity.Product;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;

public class EntityDtoUtil {

    //convert product entity to dto
    public static ProductDto toDto(Product product){
        var dto= new ProductDto();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }
}
