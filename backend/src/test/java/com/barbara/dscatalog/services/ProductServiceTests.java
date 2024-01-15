package com.barbara.dscatalog.services;

import com.barbara.dscatalog.dto.ProductDTO;
import com.barbara.dscatalog.entities.Category;
import com.barbara.dscatalog.entities.Product;
import com.barbara.dscatalog.repositories.CategoryRepository;
import com.barbara.dscatalog.repositories.ProductRepository;
import com.barbara.dscatalog.services.exceptions.DatabaseException;
import com.barbara.dscatalog.services.exceptions.ResourceNotFoundException;
import com.barbara.dscatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;


    //configurar o comportamento do repository
    //deleteById

    private long existId;
    private long noExistId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;

    private Category category;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {
        existId = 1L;
        noExistId = 1000L;
        dependentId = 4L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        category = Factory.createCategory();
        productDTO = Factory.createProductDTO();


        //simulando o comportamento esperadO do repository
        //podemos usar somente o doNothing sem o Mockito na frente devido aos imports staticos.
        Mockito.doNothing().when(repository).deleteById(existId);

        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(noExistId);

        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);


        //comportamento findAll
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);


        //comportamento save
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(page);


        //comportamento findById quando id existe
        Mockito.when(repository.findById(existId)).thenReturn(Optional.of(product));

        //comportamento findById quando id não existe
        Mockito.when(repository.findById(noExistId)).thenReturn(Optional.empty());


        //comportamento update
        Mockito.when(repository.getOne(existId)).thenReturn(product);
        Mockito.when(repository.getOne(noExistId)).thenThrow(EntityNotFoundException.class);


        Mockito.when(categoryRepository.getOne(existId)).thenReturn(category);
        Mockito.when(categoryRepository.getOne(noExistId)).thenThrow(EntityNotFoundException.class);

    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        //assertion para quando não se lança exceptions
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existId);
        });

        //verificando se o deleteById do repository foi chamado
        Mockito.verify(repository, Mockito.times(1)).deleteById(existId);
    }

    @Test
    public void deleteShouldThrowExceptionWhenIdNotExist() {
        //assertion para quando não se lança exceptions
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(noExistId);
        });

        //verificando se o deleteById do repository foi chamado
        Mockito.verify(repository, Mockito.times(1)).deleteById(existId);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        //assertion para quando não se lança exceptions
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

        //verificando se o deleteById do repository foi chamado
        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
    }


    @Test
    public void findAllPagedShouldReturnAllProductsWhenCalled() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {

        ProductDTO result = service.findById(existId);

        Assertions.assertNotNull(result);

        Mockito.verify(repository, Mockito.times(1)).findById(existId);
    }


    @Test
    public void findByIdShouldReturnResourceNotFoundExceptionWhenIdNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(noExistId);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(noExistId);
    }

    @Test
    public void updateShouldReturnProductWhenIdExists() {

        ProductDTO result = service.update(existId, productDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void updateShouldReturnResourceNotFoundExceptionWhenIdNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(noExistId, productDTO);
        });

        Mockito.verify(repository, Mockito.times(1)).getOne(noExistId);
    }


}
