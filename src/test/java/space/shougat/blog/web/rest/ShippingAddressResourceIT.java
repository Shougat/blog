package space.shougat.blog.web.rest;

import space.shougat.blog.BlogApp;
import space.shougat.blog.domain.ShippingAddress;
import space.shougat.blog.repository.ShippingAddressRepository;
import space.shougat.blog.repository.search.ShippingAddressSearchRepository;

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
 * Integration tests for the {@link ShippingAddressResource} REST controller.
 */
@SpringBootTest(classes = BlogApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ShippingAddressResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ALTERNATIVE_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ALTERNATIVE_PHONE_NUMBER = "BBBBBBBBBB";

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    /**
     * This repository is mocked in the space.shougat.blog.repository.search test package.
     *
     * @see space.shougat.blog.repository.search.ShippingAddressSearchRepositoryMockConfiguration
     */
    @Autowired
    private ShippingAddressSearchRepository mockShippingAddressSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShippingAddressMockMvc;

    private ShippingAddress shippingAddress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShippingAddress createEntity(EntityManager em) {
        ShippingAddress shippingAddress = new ShippingAddress()
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .alternativePhoneNumber(DEFAULT_ALTERNATIVE_PHONE_NUMBER);
        return shippingAddress;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShippingAddress createUpdatedEntity(EntityManager em) {
        ShippingAddress shippingAddress = new ShippingAddress()
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .alternativePhoneNumber(UPDATED_ALTERNATIVE_PHONE_NUMBER);
        return shippingAddress;
    }

    @BeforeEach
    public void initTest() {
        shippingAddress = createEntity(em);
    }

    @Test
    @Transactional
    public void createShippingAddress() throws Exception {
        int databaseSizeBeforeCreate = shippingAddressRepository.findAll().size();
        // Create the ShippingAddress
        restShippingAddressMockMvc.perform(post("/api/shipping-addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(shippingAddress)))
            .andExpect(status().isCreated());

        // Validate the ShippingAddress in the database
        List<ShippingAddress> shippingAddressList = shippingAddressRepository.findAll();
        assertThat(shippingAddressList).hasSize(databaseSizeBeforeCreate + 1);
        ShippingAddress testShippingAddress = shippingAddressList.get(shippingAddressList.size() - 1);
        assertThat(testShippingAddress.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testShippingAddress.getAlternativePhoneNumber()).isEqualTo(DEFAULT_ALTERNATIVE_PHONE_NUMBER);

        // Validate the ShippingAddress in Elasticsearch
        verify(mockShippingAddressSearchRepository, times(1)).save(testShippingAddress);
    }

    @Test
    @Transactional
    public void createShippingAddressWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = shippingAddressRepository.findAll().size();

        // Create the ShippingAddress with an existing ID
        shippingAddress.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restShippingAddressMockMvc.perform(post("/api/shipping-addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(shippingAddress)))
            .andExpect(status().isBadRequest());

        // Validate the ShippingAddress in the database
        List<ShippingAddress> shippingAddressList = shippingAddressRepository.findAll();
        assertThat(shippingAddressList).hasSize(databaseSizeBeforeCreate);

        // Validate the ShippingAddress in Elasticsearch
        verify(mockShippingAddressSearchRepository, times(0)).save(shippingAddress);
    }


    @Test
    @Transactional
    public void getAllShippingAddresses() throws Exception {
        // Initialize the database
        shippingAddressRepository.saveAndFlush(shippingAddress);

        // Get all the shippingAddressList
        restShippingAddressMockMvc.perform(get("/api/shipping-addresses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shippingAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].alternativePhoneNumber").value(hasItem(DEFAULT_ALTERNATIVE_PHONE_NUMBER)));
    }
    
    @Test
    @Transactional
    public void getShippingAddress() throws Exception {
        // Initialize the database
        shippingAddressRepository.saveAndFlush(shippingAddress);

        // Get the shippingAddress
        restShippingAddressMockMvc.perform(get("/api/shipping-addresses/{id}", shippingAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shippingAddress.getId().intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.alternativePhoneNumber").value(DEFAULT_ALTERNATIVE_PHONE_NUMBER));
    }
    @Test
    @Transactional
    public void getNonExistingShippingAddress() throws Exception {
        // Get the shippingAddress
        restShippingAddressMockMvc.perform(get("/api/shipping-addresses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateShippingAddress() throws Exception {
        // Initialize the database
        shippingAddressRepository.saveAndFlush(shippingAddress);

        int databaseSizeBeforeUpdate = shippingAddressRepository.findAll().size();

        // Update the shippingAddress
        ShippingAddress updatedShippingAddress = shippingAddressRepository.findById(shippingAddress.getId()).get();
        // Disconnect from session so that the updates on updatedShippingAddress are not directly saved in db
        em.detach(updatedShippingAddress);
        updatedShippingAddress
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .alternativePhoneNumber(UPDATED_ALTERNATIVE_PHONE_NUMBER);

        restShippingAddressMockMvc.perform(put("/api/shipping-addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedShippingAddress)))
            .andExpect(status().isOk());

        // Validate the ShippingAddress in the database
        List<ShippingAddress> shippingAddressList = shippingAddressRepository.findAll();
        assertThat(shippingAddressList).hasSize(databaseSizeBeforeUpdate);
        ShippingAddress testShippingAddress = shippingAddressList.get(shippingAddressList.size() - 1);
        assertThat(testShippingAddress.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testShippingAddress.getAlternativePhoneNumber()).isEqualTo(UPDATED_ALTERNATIVE_PHONE_NUMBER);

        // Validate the ShippingAddress in Elasticsearch
        verify(mockShippingAddressSearchRepository, times(1)).save(testShippingAddress);
    }

    @Test
    @Transactional
    public void updateNonExistingShippingAddress() throws Exception {
        int databaseSizeBeforeUpdate = shippingAddressRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShippingAddressMockMvc.perform(put("/api/shipping-addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(shippingAddress)))
            .andExpect(status().isBadRequest());

        // Validate the ShippingAddress in the database
        List<ShippingAddress> shippingAddressList = shippingAddressRepository.findAll();
        assertThat(shippingAddressList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ShippingAddress in Elasticsearch
        verify(mockShippingAddressSearchRepository, times(0)).save(shippingAddress);
    }

    @Test
    @Transactional
    public void deleteShippingAddress() throws Exception {
        // Initialize the database
        shippingAddressRepository.saveAndFlush(shippingAddress);

        int databaseSizeBeforeDelete = shippingAddressRepository.findAll().size();

        // Delete the shippingAddress
        restShippingAddressMockMvc.perform(delete("/api/shipping-addresses/{id}", shippingAddress.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ShippingAddress> shippingAddressList = shippingAddressRepository.findAll();
        assertThat(shippingAddressList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ShippingAddress in Elasticsearch
        verify(mockShippingAddressSearchRepository, times(1)).deleteById(shippingAddress.getId());
    }

    @Test
    @Transactional
    public void searchShippingAddress() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        shippingAddressRepository.saveAndFlush(shippingAddress);
        when(mockShippingAddressSearchRepository.search(queryStringQuery("id:" + shippingAddress.getId())))
            .thenReturn(Collections.singletonList(shippingAddress));

        // Search the shippingAddress
        restShippingAddressMockMvc.perform(get("/api/_search/shipping-addresses?query=id:" + shippingAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shippingAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].alternativePhoneNumber").value(hasItem(DEFAULT_ALTERNATIVE_PHONE_NUMBER)));
    }
}
