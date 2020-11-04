package com.camel.solution.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String custId;
    private String prdctId;
    private String ordStatus;
    private Integer qty;
}
