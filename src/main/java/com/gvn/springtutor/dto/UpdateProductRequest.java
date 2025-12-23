package com.gvn.springtutor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk request update Product.
 * Semua field optional - hanya field yang diisi yang akan diupdate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    private String name;

    private Integer tenor;

    private Double interestRate;
}
