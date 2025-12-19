package com.gvn.springtutor.base;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponse - Generic wrapper untuk standarisasi response REST API.
 * 
 * <p>
 * Class ini digunakan untuk membungkus semua response API agar memiliki
 * format yang konsisten, memudahkan frontend dalam parsing response,
 * dan menyediakan informasi tambahan seperti status, message, dan timestamp.
 * </p>
 * 
 * <p>
 * Menggunakan Generic Type {@code <T>} agar bisa membungkus berbagai tipe data.
 * </p>
 * 
 * @param <T> Tipe data yang akan dibungkus dalam field 'data'
 * 
 * @author spring-tutor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** Menandakan apakah request berhasil atau gagal */
    private Boolean success;

    /** Pesan informatif untuk user/developer */
    private String message;

    /** Data payload (bisa berupa object, list, atau null) */
    private T data;

    /** HTTP status code (200, 201, 400, 404, 500, dll) */
    private Integer code;

    /** Waktu response dibuat */
    private Instant timestamp;

    /**
     * Factory method untuk membuat success response.
     */

}