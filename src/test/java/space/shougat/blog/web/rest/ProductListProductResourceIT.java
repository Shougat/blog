package space.shougat.blog.web.rest;

import space.shougat.blog.BlogApp;
import space.shougat.blog.domain.ProductListProduct;
import space.shougat.blog.repository.ProductListProductRepository;
import space.shougat.blog.repository.search.ProductListProductSearchRepository;

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
 * Integration tests for the {@link ProductListProductResource} REST controller.
 */
@SpringBootTest(classes = BlogApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ProductListProductResourceIT {

    @Autowired
    private ProductListProductRepository productListProductRepository;

    /**
     * This repository is mocked in the space.shougat.blog.repository.search test package.
     *
     * @see space.shougat.blog.repository.search.ProductListProductSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProductListProductSearchRepository mockProductListProductSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductListProductMockMvc;

    private ProductListProduct productListProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductListProduct createEntity(EntityManager em) {
        ProductListProduct productListProduct = new ProductListProduct();
        return productListProduct;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductListProduct createUpdatedEntity(EntityManager em) {
        ProductListProduct productListProduct = new ProductListProduct();
        return productListProduct;
    }

    @BeforeEach
    public void initTest() {
        productListProduct = createEntity(em);
    }

    @Test
    @Transactional
    public void createProductListProduct() throws Exception {
        int databaseSizeBeforeCreate = productListProductRepository.findAll().size();
        // Create the ProductListProduct
        restProductListProductMockMvc.perform(post("/api/product-list-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productListProduct)))
            .andExpect(status().isCreated());

        // Validate the ProductListProduct in the database
        List<ProductListProduct> productListProductList = productListProductRepository.findAll();
        assertThat(productListProductList).hasSize(databaseSizeBeforeCreate + 1);
        ProductListProduct testProductListProduct = productListProductList.get(productListProductList.size() - 1);

        // Validate the ProductListProduct in Elasticsearch
        verify(mockProductListProductSearchRepository, times(1)).save(testProductListProduct);
    }

    @Test
    @Transactional
    public void createProductListProductWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productListProductRepository.findAll().size();

        // Create the ProductListProduct with an existing ID
        productListProduct.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductListProductMockMvc.perform(post("/api/product-list-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productListProduct)))
            .andExpect(status().isBadRequest());

        // Validate the ProductListProduct in the database
        List<ProductListProduct> productListProductList = productListProductRepository.findAll();
        assertThat(productListProductList).hasSize(databaseSizeBeforeCreate);

        // Validate the ProductListProduct in Elasticsearch
        verify(mockProductListProductSearchRepository, times(0)).save(productListProduct);
    }


    @Test
    @Transactional
    public void getAllProductListProducts() throws Exception {
        // Initialize the database
        productListProductRepository.saveAndFlush(productListProduct);

        // Get all the productListProductList
        restProductListProductMockMvc.perform(get("/api/product-list-products?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productListProduct.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getProductListProduct() throws Exception {
        // Initialize the database
        productListProductRepository.saveAndFlush(productListProduct);

        // Get the productListProduct
        restProductListProductMockMvc.perform(get("/api/product-list-products/{id}", productListProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productListProduct.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingProductListProduct() throws Exception {
        // Get the productListProduct
        restProductListProductMockMvc.perform(get("/api/product-list-products/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductListProduct() throws Exception {
        // Initialize the database
        productListProductRepository.saveAndFlush(productListProduct);

        int databaseSizeBeforeUpdate = productListProductRepository.findAll().size();

        // Update the productListProduct
        ProductListProduct updatedProductListProduct = productListProductRepository.findById(productListProduct.getId()).get();
        // Disconnect from session so that the updates on updatedProductListProduct are not directly saved in db
        em.detach(updatedProductListProduct);

        restProductListProductMockMvc.perform(put("/api/product-list-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedProductListProduct)))
            .andExpect(status().isOk());

        // Validate the ProductListProduct in the database
        List<ProductListProduct> productListProductList = productListProductRepository.findAll();
        assertThat(productListProductList).hasSize(databaseSizeBeforeUpdate);
        ProductListProduct testProductListProduct = productListProductList.get(productListProductList.size() - 1);

        // Validate the ProductListProduct in Elasticsearch
        verify(mockProductListProductSearchRepository, times(1)).save(testProductListProduct);
    }

    @Test
    @Transactional
    public void updateNonExistingProductListProduct() throws Exception {
        int databaseSizeBeforeUpdate = productListProductRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductListProductMockMvc.perform(put("/api/product-list-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productListProduct)))
            .andExpect(status().isBadRequest());

        // Validate the ProductListProduct in the database
        List<ProductListProduct> productListProductList = productListProductRepository.findAll();
        assertThat(productListProductList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProductListProduct in Elasticsearch
        verify(mockProductListProductSearchRepository, times(0)).save(productListProduct);
    }

    @Test
    @Transactional
    public void deleteProductListProduct() throws Exception {
        // Initialize the database
        productListProductRepository.saveAndFlush(productListProduct);

        int databaseSizeBeforeDelete = productListProductRepository.findAll().size();

        // Delete the productListProduct
        restProductListProductMockMvc.perform(delete("/api/product-list-products/{id}", productListProduct.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductListProduct> productListProductList = productListProductRepository.findAll();
        assertThat(productListProductList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ProductListProduct in Elasticsearch
        verify(mockProductListProductSearchRepository, times(1)).deleteById(productListProduct.getId());
    }

    @Test
    @Transactional
    public void searchProductListProduct() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        productListProductRepository.saveAndFlush(productListProduct);
        when(mockProductListProductSearchRepository.search(queryStringQuery("id:" + productListProduct.getId())))
            .thenReturn(Collections.singletonList(productListProduct));

        // Search the productListProduct
        restProductListProductMockMvc.perform(get("/api/_search/product-list-products?query=id:" + productListProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productListProduct.getId().intValue())));
    }
}
