package space.shougat.blog.web.rest;

import space.shougat.blog.BlogApp;
import space.shougat.blog.domain.ProductList;
import space.shougat.blog.repository.ProductListRepository;
import space.shougat.blog.repository.search.ProductListSearchRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ProductListResource} REST controller.
 */
@SpringBootTest(classes = BlogApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ProductListResourceIT {

    @Autowired
    private ProductListRepository productListRepository;

    /**
     * This repository is mocked in the space.shougat.blog.repository.search test package.
     *
     * @see space.shougat.blog.repository.search.ProductListSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProductListSearchRepository mockProductListSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductListMockMvc;

    private ProductList productList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductList createEntity(EntityManager em) {
        ProductList productList = new ProductList();
        return productList;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductList createUpdatedEntity(EntityManager em) {
        ProductList productList = new ProductList();
        return productList;
    }

    @BeforeEach
    public void initTest() {
        productList = createEntity(em);
    }

    @Test
    @Transactional
    public void createProductList() throws Exception {
        int databaseSizeBeforeCreate = productListRepository.findAll().size();
        // Create the ProductList
        restProductListMockMvc.perform(post("/api/product-lists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productList)))
            .andExpect(status().isCreated());

        // Validate the ProductList in the database
        List<ProductList> productListList = productListRepository.findAll();
        assertThat(productListList).hasSize(databaseSizeBeforeCreate + 1);
        ProductList testProductList = productListList.get(productListList.size() - 1);

        // Validate the ProductList in Elasticsearch
        verify(mockProductListSearchRepository, times(1)).save(testProductList);
    }

    @Test
    @Transactional
    public void createProductListWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productListRepository.findAll().size();

        // Create the ProductList with an existing ID
        productList.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductListMockMvc.perform(post("/api/product-lists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productList)))
            .andExpect(status().isBadRequest());

        // Validate the ProductList in the database
        List<ProductList> productListList = productListRepository.findAll();
        assertThat(productListList).hasSize(databaseSizeBeforeCreate);

        // Validate the ProductList in Elasticsearch
        verify(mockProductListSearchRepository, times(0)).save(productList);
    }


    @Test
    @Transactional
    public void getAllProductLists() throws Exception {
        // Initialize the database
        productListRepository.saveAndFlush(productList);

        // Get all the productListList
        restProductListMockMvc.perform(get("/api/product-lists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productList.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getProductList() throws Exception {
        // Initialize the database
        productListRepository.saveAndFlush(productList);

        // Get the productList
        restProductListMockMvc.perform(get("/api/product-lists/{id}", productList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productList.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingProductList() throws Exception {
        // Get the productList
        restProductListMockMvc.perform(get("/api/product-lists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductList() throws Exception {
        // Initialize the database
        productListRepository.saveAndFlush(productList);

        int databaseSizeBeforeUpdate = productListRepository.findAll().size();

        // Update the productList
        ProductList updatedProductList = productListRepository.findById(productList.getId()).get();
        // Disconnect from session so that the updates on updatedProductList are not directly saved in db
        em.detach(updatedProductList);

        restProductListMockMvc.perform(put("/api/product-lists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedProductList)))
            .andExpect(status().isOk());

        // Validate the ProductList in the database
        List<ProductList> productListList = productListRepository.findAll();
        assertThat(productListList).hasSize(databaseSizeBeforeUpdate);
        ProductList testProductList = productListList.get(productListList.size() - 1);

        // Validate the ProductList in Elasticsearch
        verify(mockProductListSearchRepository, times(1)).save(testProductList);
    }

    @Test
    @Transactional
    public void updateNonExistingProductList() throws Exception {
        int databaseSizeBeforeUpdate = productListRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductListMockMvc.perform(put("/api/product-lists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productList)))
            .andExpect(status().isBadRequest());

        // Validate the ProductList in the database
        List<ProductList> productListList = productListRepository.findAll();
        assertThat(productListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProductList in Elasticsearch
        verify(mockProductListSearchRepository, times(0)).save(productList);
    }

    @Test
    @Transactional
    public void deleteProductList() throws Exception {
        // Initialize the database
        productListRepository.saveAndFlush(productList);

        int databaseSizeBeforeDelete = productListRepository.findAll().size();

        // Delete the productList
        restProductListMockMvc.perform(delete("/api/product-lists/{id}", productList.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductList> productListList = productListRepository.findAll();
        assertThat(productListList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ProductList in Elasticsearch
        verify(mockProductListSearchRepository, times(1)).deleteById(productList.getId());
    }

    @Test
    @Transactional
    public void searchProductList() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        productListRepository.saveAndFlush(productList);
        when(mockProductListSearchRepository.search(queryStringQuery("id:" + productList.getId())))
            .thenReturn(Collections.singletonList(productList));

        // Search the productList
        restProductListMockMvc.perform(get("/api/_search/product-lists?query=id:" + productList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productList.getId().intValue())));
    }
}
