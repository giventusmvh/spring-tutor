package com.gvn.springtutor.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.gvn.springtutor.entity.Product;
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
}
