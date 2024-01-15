package com.barbara.dscatalog.repositories;

import com.barbara.dscatalog.entities.Product;
import com.barbara.dscatalog.services.exceptions.ResourceNotFoundException;
import com.barbara.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existId;
    private long noExistId;
    private long countTotalProducts;
    @BeforeEach
    void setUp() throws Exception {
        existId = 1L;
        noExistId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existId);

        Optional<Product> result =  repository.findById(existId);

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void deleteShouldShowExceptionWhenIdNotFound() {

        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            repository.deleteById(noExistId);
        });
    }

    @Test
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
        Product product = Factory.createProduct();

        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(26L, product.getId());
    }

    @Test
    public void findByIdShouldReturnOptinalNotEmptyWhenExistsId() {
        //o findById SEMPRE retornará um optional pois o elemento pode não existir

        Optional<Product> product = repository.findById(existId);

        Assertions.assertTrue(product.isPresent());
    }


    @Test
    public void findByIdShouldReturnOptinalEmptyWhenNotExistsId() {
        //o findById SEMPRE retornará um optional pois o elemento pode não existir

        Optional<Product> product = repository.findById(noExistId);

        Assertions.assertTrue(product.isEmpty());
    }



}
