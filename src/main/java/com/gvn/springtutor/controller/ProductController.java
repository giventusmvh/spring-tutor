package com.gvn.springtutor.controller;

import com.gvn.springtutor.base.ApiResponse;
import com.gvn.springtutor.entity.Product;
import com.gvn.springtutor.service.ProductService;
import com.gvn.springtutor.util.ResponseUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller untuk Product.
 * 
 * Menggunakan ResponseUtil untuk standarisasi response API.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /products - Mengambil semua product.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseUtil.ok(products, "Products retrieved successfully");
    }

    /**
     * POST /products - Membuat product baru.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.createProduct(product);
        return ResponseUtil.created(savedProduct, "Product created successfully");
    }
}
