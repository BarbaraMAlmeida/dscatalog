package com.barbara.dscatalog.services;

import com.barbara.dscatalog.dto.ProductDTO;
import com.barbara.dscatalog.repositories.ProductRepository;
import com.barbara.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
//evita a dependencia de um teste com outro recarregando o banco após rodar cada teste
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long noExistId;
    private Long countTotalProducts;
    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        noExistId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingId);

        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }


    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(noExistId);
        });
    }

    @Test
    public void findAllShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());
    }

    @Test
    public void findAllShouldReturnPageEmptyWhenPageNotExist() {
        Pageable pageable = PageRequest.of(50, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllShouldReturnPageCorrectOrderWhenSortByName() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
    }
}
