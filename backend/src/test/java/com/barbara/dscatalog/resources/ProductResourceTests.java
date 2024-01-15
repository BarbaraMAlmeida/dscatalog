package com.barbara.dscatalog.resources;

import com.barbara.dscatalog.dto.ProductDTO;
import com.barbara.dscatalog.entities.Product;
import com.barbara.dscatalog.services.ProductService;
import com.barbara.dscatalog.services.exceptions.DatabaseException;
import com.barbara.dscatalog.services.exceptions.ResourceNotFoundException;
import com.barbara.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

    //trabalharemos com a chamada de endpoints e pra isso usamos o mockMvc
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private PageImpl<ProductDTO> page;
    private ProductDTO productdto;

    private long existId;

    private long notExistId;

    private long dependentId;

    @BeforeEach
    void setUp() throws Exception {
        existId = 1L;
        notExistId = 1000L;
        dependentId = 2L;
        productdto = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productdto));

        Mockito.when(service.findAllPaged((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(service.findById(existId)).thenReturn(productdto);

        Mockito.when(service.findById(notExistId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(service.update(ArgumentMatchers.eq(existId), ArgumentMatchers.any())).thenReturn(productdto);

        Mockito.when(service.update(ArgumentMatchers.eq(notExistId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);

        //EM METODOS VOID PRIMEIRO COLOCA-SE A CONSEQUENCIA
        //Delete
        Mockito.doNothing().when(service).delete(existId);

        Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(notExistId);

        Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);

        //insert
        Mockito.when(service.insert(ArgumentMatchers.any())).thenReturn(productdto);
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }


    @Test
    public void findByIdShouldReturnProductWhenIdExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/{id}", existId).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        //testando se na resposta do endpoint, se existe o campo ID.
        // o $ acessa o objeto json da resposta
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdNotExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/{id}", notExistId));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExist() throws Exception {
        //converter objeto java para json
        String jsonBody = objectMapper.writeValueAsString(productdto);

        ResultActions result = mockMvc.perform(put("/products/{id}", existId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productdto);

        ResultActions result = mockMvc.perform(put("/products/{id}", notExistId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExist() throws Exception {
        ResultActions result = mockMvc.perform(delete("/products/{id}", existId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }


    @Test
    public void deleteShouldReturnNotFoundtWhenIdNotExist() throws Exception {
        ResultActions result = mockMvc.perform(delete("/products/{id}", notExistId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnCreatedAndProductDto() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productdto);

        ResultActions result = mockMvc.perform(post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
    }

}
