package com.barbara.dscatalog.resources;

import com.barbara.dscatalog.dto.ProductDTO;
import com.barbara.dscatalog.services.ProductService;
import com.barbara.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceIT {

    @Autowired
    private MockMvc mockMvc;

    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;
    private Long existingId;
    private Long noExistId;
    private Long countTotalProducts;

    private ProductDTO productDTO;
    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        noExistId = 1000L;
        countTotalProducts = 25L;
        productDTO = Factory.createProductDTO();
    }

    @Test
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        ResultActions result = mockMvc.perform(get("/products?page=0&size=12&sort=name")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
    }


    @Test
    public void updateShouldReturnProductDTOWhenIdExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();

        ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.name").value(expectedName));
        result.andExpect(jsonPath("$.description").value(expectedDescription));
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();

        ResultActions result = mockMvc.perform(put("/products/{id}", noExistId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

}
