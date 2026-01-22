package com.app.ecom.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CartItemRequest {

    private Long productId;
    private Integer quantity;


}
