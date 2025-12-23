package com.gvn.springtutor.controller;

import com.gvn.springtutor.base.ApiResponse;
import com.gvn.springtutor.entity.Product;
import com.gvn.springtutor.service.ProductService;
import com.gvn.springtutor.util.ResponseUtil;
import lombok.RequiredArgsConstructor;

//import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * GET /products/{id} - Mengambil product berdasarkan ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseUtil.ok(product, "Product retrieved successfully");
    }

    /**
     * PUT /products/{id} - Update product berdasarkan ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseUtil.ok(updatedProduct, "Product updated successfully");
    }

    /**
     * DELETE /products/{id} - Hapus product berdasarkan ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseUtil.ok(null, "Product deleted successfully");
    }
}
