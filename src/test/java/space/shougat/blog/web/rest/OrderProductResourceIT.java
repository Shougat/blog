package space.shougat.blog.web.rest;

import space.shougat.blog.BlogApp;
import space.shougat.blog.domain.OrderProduct;
import space.shougat.blog.repository.OrderProductRepository;
import space.shougat.blog.repository.search.OrderProductSearchRepository;

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
 * Integration tests for the {@link OrderProductResource} REST controller.
 */
@SpringBootTest(classes = BlogApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class OrderProductResourceIT {

    @Autowired
    private OrderProductRepository orderProductRepository;

    /**
     * This repository is mocked in the space.shougat.blog.repository.search test package.
     *
     * @see space.shougat.blog.repository.search.OrderProductSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrderProductSearchRepository mockOrderProductSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderProductMockMvc;

    private OrderProduct orderProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderProduct createEntity(EntityManager em) {
        OrderProduct orderProduct = new OrderProduct();
        return orderProduct;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderProduct createUpdatedEntity(EntityManager em) {
        OrderProduct orderProduct = new OrderProduct();
        return orderProduct;
    }

    @BeforeEach
    public void initTest() {
        orderProduct = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrderProduct() throws Exception {
        int databaseSizeBeforeCreate = orderProductRepository.findAll().size();
        // Create the OrderProduct
        restOrderProductMockMvc.perform(post("/api/order-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderProduct)))
            .andExpect(status().isCreated());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeCreate + 1);
        OrderProduct testOrderProduct = orderProductList.get(orderProductList.size() - 1);

        // Validate the OrderProduct in Elasticsearch
        verify(mockOrderProductSearchRepository, times(1)).save(testOrderProduct);
    }

    @Test
    @Transactional
    public void createOrderProductWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = orderProductRepository.findAll().size();

        // Create the OrderProduct with an existing ID
        orderProduct.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderProductMockMvc.perform(post("/api/order-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderProduct)))
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeCreate);

        // Validate the OrderProduct in Elasticsearch
        verify(mockOrderProductSearchRepository, times(0)).save(orderProduct);
    }


    @Test
    @Transactional
    public void getAllOrderProducts() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList
        restOrderProductMockMvc.perform(get("/api/order-products?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderProduct.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getOrderProduct() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get the orderProduct
        restOrderProductMockMvc.perform(get("/api/order-products/{id}", orderProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderProduct.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingOrderProduct() throws Exception {
        // Get the orderProduct
        restOrderProductMockMvc.perform(get("/api/order-products/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderProduct() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();

        // Update the orderProduct
        OrderProduct updatedOrderProduct = orderProductRepository.findById(orderProduct.getId()).get();
        // Disconnect from session so that the updates on updatedOrderProduct are not directly saved in db
        em.detach(updatedOrderProduct);

        restOrderProductMockMvc.perform(put("/api/order-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedOrderProduct)))
            .andExpect(status().isOk());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);
        OrderProduct testOrderProduct = orderProductList.get(orderProductList.size() - 1);

        // Validate the OrderProduct in Elasticsearch
        verify(mockOrderProductSearchRepository, times(1)).save(testOrderProduct);
    }

    @Test
    @Transactional
    public void updateNonExistingOrderProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderProductMockMvc.perform(put("/api/order-products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderProduct)))
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderProduct in Elasticsearch
        verify(mockOrderProductSearchRepository, times(0)).save(orderProduct);
    }

    @Test
    @Transactional
    public void deleteOrderProduct() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        int databaseSizeBeforeDelete = orderProductRepository.findAll().size();

        // Delete the orderProduct
        restOrderProductMockMvc.perform(delete("/api/order-products/{id}", orderProduct.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the OrderProduct in Elasticsearch
        verify(mockOrderProductSearchRepository, times(1)).deleteById(orderProduct.getId());
    }

    @Test
    @Transactional
    public void searchOrderProduct() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);
        when(mockOrderProductSearchRepository.search(queryStringQuery("id:" + orderProduct.getId())))
            .thenReturn(Collections.singletonList(orderProduct));

        // Search the orderProduct
        restOrderProductMockMvc.perform(get("/api/_search/order-products?query=id:" + orderProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderProduct.getId().intValue())));
    }
}
