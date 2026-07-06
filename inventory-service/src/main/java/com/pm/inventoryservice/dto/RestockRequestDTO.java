package com.pm.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RestockRequestDTO {

    @Min(1)
    private Integer quantity;
}