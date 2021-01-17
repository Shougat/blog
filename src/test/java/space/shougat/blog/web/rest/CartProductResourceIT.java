package space.shougat.blog.web.rest;

import space.shougat.blog.BlogApp;
import space.shougat.blog.domain.CartProduct;
import space.shougat.blog.repository.CartProductRepository;
import space.shougat.blog.repository.search.CartProductSearchRepository;

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
 * Integration tests for the {@link CartProductResource} REST controller.
 */
@SpringBootTest(classes = BlogApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class CartProductResourceIT {

    @Autowired
    private CartProductRepository cartProductRepository;

    /**
     * This repository is mocked in the space.shougat.blog.repository.search test package.
     *
     * @see space.shougat.blog.repository.search.CartProductSearchRepositoryMockConfiguration
     */
    @Autowired
    private CartProductSearchRepository mockCartProductSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartProductMockMvc;

    private CartProduct cartProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartProduct createEntity(EntityManager em) {
        CartProduct cartProduct = new CartProduct();
        return cartProduct;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartProduct createUpdatedEntity(EntityManager em) {
        CartProduct cartProduct = new CartProduct();
        return cartProduct;
    }

    @BeforeEach
    public void initTest() {
        cartProduct = createEntity(em);
    }

    @Test
    @Transactional
    public void createCartProduct() throws Exception {
        int databaseSizeBeforeCreate = cartProductRepository.findAll().size();
        // Create the CartProduct
        restCartProductMockMvc.perform(post("/api/cart-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(cartProduct)))
            .andExpect(status().isCreated());

        // Validate the CartProduct in the database
        List<CartProduct> cartProductList = cartProductRepository.findAll();
        assertThat(cartProductList).hasSize(databaseSizeBeforeCreate + 1);
        CartProduct testCartProduct = cartProductList.get(cartProductList.size() - 1);

        // Validate the CartProduct in Elasticsearch
        verify(mockCartProductSearchRepository, times(1)).save(testCartProduct);
    }

    @Test
    @Transactional
    public void createCartProductWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cartProductRepository.findAll().size();

        // Create the CartProduct with an existing ID
        cartProduct.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartProductMockMvc.perform(post("/api/cart-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(cartProduct)))
            .andExpect(status().isBadRequest());

        // Validate the CartProduct in the database
        List<CartProduct> cartProductList = cartProductRepository.findAll();
        assertThat(cartProductList).hasSize(databaseSizeBeforeCreate);

        // Validate the CartProduct in Elasticsearch
        verify(mockCartProductSearchRepository, times(0)).save(cartProduct);
    }


    @Test
    @Transactional
    public void getAllCartProducts() throws Exception {
        // Initialize the database
        cartProductRepository.saveAndFlush(cartProduct);

        // Get all the cartProductList
        restCartProductMockMvc.perform(get("/api/cart-products?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartProduct.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getCartProduct() throws Exception {
        // Initialize the database
        cartProductRepository.saveAndFlush(cartProduct);

        // Get the cartProduct
        restCartProductMockMvc.perform(get("/api/cart-products/{id}", cartProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cartProduct.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingCartProduct() throws Exception {
        // Get the cartProduct
        restCartProductMockMvc.perform(get("/api/cart-products/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCartProduct() throws Exception {
        // Initialize the database
        cartProductRepository.saveAndFlush(cartProduct);

        int databaseSizeBeforeUpdate = cartProductRepository.findAll().size();

        // Update the cartProduct
        CartProduct updatedCartProduct = cartProductRepository.findById(cartProduct.getId()).get();
        // Disconnect from session so that the updates on updatedCartProduct are not directly saved in db
        em.detach(updatedCartProduct);

        restCartProductMockMvc.perform(put("/api/cart-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCartProduct)))
            .andExpect(status().isOk());

        // Validate the CartProduct in the database
        List<CartProduct> cartProductList = cartProductRepository.findAll();
        assertThat(cartProductList).hasSize(databaseSizeBeforeUpdate);
        CartProduct testCartProduct = cartProductList.get(cartProductList.size() - 1);

        // Validate the CartProduct in Elasticsearch
        verify(mockCartProductSearchRepository, times(1)).save(testCartProduct);
    }

    @Test
    @Transactional
    public void updateNonExistingCartProduct() throws Exception {
        int databaseSizeBeforeUpdate = cartProductRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartProductMockMvc.perform(put("/api/cart-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(cartProduct)))
            .andExpect(status().isBadRequest());

        // Validate the CartProduct in the database
        List<CartProduct> cartProductList = cartProductRepository.findAll();
        assertThat(cartProductList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CartProduct in Elasticsearch
        verify(mockCartProductSearchRepository, times(0)).save(cartProduct);
    }

    @Test
    @Transactional
    public void deleteCartProduct() throws Exception {
        // Initialize the database
        cartProductRepository.saveAndFlush(cartProduct);

        int databaseSizeBeforeDelete = cartProductRepository.findAll().size();

        // Delete the cartProduct
        restCartProductMockMvc.perform(delete("/api/cart-products/{id}", cartProduct.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CartProduct> cartProductList = cartProductRepository.findAll();
        assertThat(cartProductList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CartProduct in Elasticsearch
        verify(mockCartProductSearchRepository, times(1)).deleteById(cartProduct.getId());
    }

    @Test
    @Transactional
    public void searchCartProduct() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        cartProductRepository.saveAndFlush(cartProduct);
        when(mockCartProductSearchRepository.search(queryStringQuery("id:" + cartProduct.getId())))
            .thenReturn(Collections.singletonList(cartProduct));

        // Search the cartProduct
        restCartProductMockMvc.perform(get("/api/_search/cart-products?query=id:" + cartProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartProduct.getId().intValue())));
    }
}
