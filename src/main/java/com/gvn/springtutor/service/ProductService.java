package com.gvn.springtutor.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.gvn.springtutor.entity.Product;
import com.gvn.springtutor.exception.ResourceNotFoundException;
import com.gvn.springtutor.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Product Service dengan Redis Caching.
 * 
 * @Cacheable - Cache hasil method. Jika data sudah ada di cache, tidak query
 *            database.
 * @CacheEvict - Hapus cache saat data berubah.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Create product dan hapus cache products.
     * 
     * @CacheEvict - Menghapus semua cache dengan name "products"
     *             allEntries=true berarti hapus semua entry di cache ini
     */
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(Product product) {
        log.info("Creating product: {}", product.getName());
        return productRepository.save(product);
    }

    /**
     * Get all products dengan caching.
     * 
     * @Cacheable - Cache hasil dengan key "allProducts"
     *            Jika cache hit, tidak akan query database
     */
    @Cacheable(value = "products", key = "'allProducts'")
    public List<Product> getAllProducts() {
        log.info("Fetching all products from DATABASE");
        return productRepository.findAll();
    }

    /**
     * Get product by ID dengan caching.
     * 
     * @Cacheable - Cache hasil dengan key berdasarkan ID
     */
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        log.info("Fetching product with ID {} from DATABASE", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    /**
     * Update product dan hapus cache.
     * 
     * @CacheEvict - Menghapus semua cache products karena data berubah
     */
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Long id, Product productDetails) {
        log.info("Updating product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        existingProduct.setName(productDetails.getName());
        existingProduct.setTenor(productDetails.getTenor());
        existingProduct.setInterestRate(productDetails.getInterestRate());

        return productRepository.save(existingProduct);
    }

    /**
     * Delete product dan hapus cache.
     * 
     * @CacheEvict - Menghapus semua cache products karena data berubah
     */
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(existingProduct);
    }
}
