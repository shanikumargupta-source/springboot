package com.example.springproject.service;

import com.example.springproject.exception.ProductNotFoundException;
import com.example.springproject.model.Product;
import com.example.springproject.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Test Product", 100.0, 10);
    }

    @Test
    void getAllProducts_ShouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        List<Product> products = productService.getAllProducts();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Optional<Product> foundProduct = productService.getProductById(1L);
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Product createdProduct = productService.createProduct(product);
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("Test Product");
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() {
        Product updatedDetails = new Product(null, "Updated Product", 150.0, 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.updateProduct(1L, updatedDetails);

        assertThat(result.getName()).isEqualTo("Updated Product");
        assertThat(result.getPrice()).isEqualTo(150.0);
    }

    @Test
    void updateProduct_WhenNotFound_ShouldThrowException() {
        Product updatedDetails = new Product(null, "Updated Product", 150.0, 5);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, updatedDetails));
    }

    @Test
    void deleteProduct_ShouldCallDelete() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProduct_WhenNotFound_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
    }
}
