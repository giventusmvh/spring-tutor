package com.gvn.springtutor.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk request update Product.
 * Semua field optional - hanya field yang diisi yang akan diupdate (partial
 * update).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    private String name;

    @Positive(message = "Tenor must be positive")
    private Integer tenor;

    @Positive(message = "Interest rate must be positive")
    private Double interestRate;
}
