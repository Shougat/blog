package space.shougat.blog.web.rest;

import space.shougat.blog.BlogApp;
import space.shougat.blog.domain.BillingAddress;
import space.shougat.blog.repository.BillingAddressRepository;
import space.shougat.blog.repository.search.BillingAddressSearchRepository;

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
 * Integration tests for the {@link BillingAddressResource} REST controller.
 */
@SpringBootTest(classes = BlogApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class BillingAddressResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ALTERNATIVE_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ALTERNATIVE_PHONE_NUMBER = "BBBBBBBBBB";

    @Autowired
    private BillingAddressRepository billingAddressRepository;

    /**
     * This repository is mocked in the space.shougat.blog.repository.search test package.
     *
     * @see space.shougat.blog.repository.search.BillingAddressSearchRepositoryMockConfiguration
     */
    @Autowired
    private BillingAddressSearchRepository mockBillingAddressSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBillingAddressMockMvc;

    private BillingAddress billingAddress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BillingAddress createEntity(EntityManager em) {
        BillingAddress billingAddress = new BillingAddress()
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .alternativePhoneNumber(DEFAULT_ALTERNATIVE_PHONE_NUMBER);
        return billingAddress;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BillingAddress createUpdatedEntity(EntityManager em) {
        BillingAddress billingAddress = new BillingAddress()
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .alternativePhoneNumber(UPDATED_ALTERNATIVE_PHONE_NUMBER);
        return billingAddress;
    }

    @BeforeEach
    public void initTest() {
        billingAddress = createEntity(em);
    }

    @Test
    @Transactional
    public void createBillingAddress() throws Exception {
        int databaseSizeBeforeCreate = billingAddressRepository.findAll().size();
        // Create the BillingAddress
        restBillingAddressMockMvc.perform(post("/api/billing-addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(billingAddress)))
            .andExpect(status().isCreated());

        // Validate the BillingAddress in the database
        List<BillingAddress> billingAddressList = billingAddressRepository.findAll();
        assertThat(billingAddressList).hasSize(databaseSizeBeforeCreate + 1);
        BillingAddress testBillingAddress = billingAddressList.get(billingAddressList.size() - 1);
        assertThat(testBillingAddress.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testBillingAddress.getAlternativePhoneNumber()).isEqualTo(DEFAULT_ALTERNATIVE_PHONE_NUMBER);

        // Validate the BillingAddress in Elasticsearch
        verify(mockBillingAddressSearchRepository, times(1)).save(testBillingAddress);
    }

    @Test
    @Transactional
    public void createBillingAddressWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = billingAddressRepository.findAll().size();

        // Create the BillingAddress with an existing ID
        billingAddress.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBillingAddressMockMvc.perform(post("/api/billing-addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(billingAddress)))
            .andExpect(status().isBadRequest());

        // Validate the BillingAddress in the database
        List<BillingAddress> billingAddressList = billingAddressRepository.findAll();
        assertThat(billingAddressList).hasSize(databaseSizeBeforeCreate);

        // Validate the BillingAddress in Elasticsearch
        verify(mockBillingAddressSearchRepository, times(0)).save(billingAddress);
    }


    @Test
    @Transactional
    public void getAllBillingAddresses() throws Exception {
        // Initialize the database
        billingAddressRepository.saveAndFlush(billingAddress);

        // Get all the billingAddressList
        restBillingAddressMockMvc.perform(get("/api/billing-addresses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(billingAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].alternativePhoneNumber").value(hasItem(DEFAULT_ALTERNATIVE_PHONE_NUMBER)));
    }
    
    @Test
    @Transactional
    public void getBillingAddress() throws Exception {
        // Initialize the database
        billingAddressRepository.saveAndFlush(billingAddress);

        // Get the billingAddress
        restBillingAddressMockMvc.perform(get("/api/billing-addresses/{id}", billingAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(billingAddress.getId().intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.alternativePhoneNumber").value(DEFAULT_ALTERNATIVE_PHONE_NUMBER));
    }
    @Test
    @Transactional
    public void getNonExistingBillingAddress() throws Exception {
        // Get the billingAddress
        restBillingAddressMockMvc.perform(get("/api/billing-addresses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBillingAddress() throws Exception {
        // Initialize the database
        billingAddressRepository.saveAndFlush(billingAddress);

        int databaseSizeBeforeUpdate = billingAddressRepository.findAll().size();

        // Update the billingAddress
        BillingAddress updatedBillingAddress = billingAddressRepository.findById(billingAddress.getId()).get();
        // Disconnect from session so that the updates on updatedBillingAddress are not directly saved in db
        em.detach(updatedBillingAddress);
        updatedBillingAddress
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .alternativePhoneNumber(UPDATED_ALTERNATIVE_PHONE_NUMBER);

        restBillingAddressMockMvc.perform(put("/api/billing-addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedBillingAddress)))
            .andExpect(status().isOk());

        // Validate the BillingAddress in the database
        List<BillingAddress> billingAddressList = billingAddressRepository.findAll();
        assertThat(billingAddressList).hasSize(databaseSizeBeforeUpdate);
        BillingAddress testBillingAddress = billingAddressList.get(billingAddressList.size() - 1);
        assertThat(testBillingAddress.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testBillingAddress.getAlternativePhoneNumber()).isEqualTo(UPDATED_ALTERNATIVE_PHONE_NUMBER);

        // Validate the BillingAddress in Elasticsearch
        verify(mockBillingAddressSearchRepository, times(1)).save(testBillingAddress);
    }

    @Test
    @Transactional
    public void updateNonExistingBillingAddress() throws Exception {
        int databaseSizeBeforeUpdate = billingAddressRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBillingAddressMockMvc.perform(put("/api/billing-addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(billingAddress)))
            .andExpect(status().isBadRequest());

        // Validate the BillingAddress in the database
        List<BillingAddress> billingAddressList = billingAddressRepository.findAll();
        assertThat(billingAddressList).hasSize(databaseSizeBeforeUpdate);

        // Validate the BillingAddress in Elasticsearch
        verify(mockBillingAddressSearchRepository, times(0)).save(billingAddress);
    }

    @Test
    @Transactional
    public void deleteBillingAddress() throws Exception {
        // Initialize the database
        billingAddressRepository.saveAndFlush(billingAddress);

        int databaseSizeBeforeDelete = billingAddressRepository.findAll().size();

        // Delete the billingAddress
        restBillingAddressMockMvc.perform(delete("/api/billing-addresses/{id}", billingAddress.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BillingAddress> billingAddressList = billingAddressRepository.findAll();
        assertThat(billingAddressList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the BillingAddress in Elasticsearch
        verify(mockBillingAddressSearchRepository, times(1)).deleteById(billingAddress.getId());
    }

    @Test
    @Transactional
    public void searchBillingAddress() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        billingAddressRepository.saveAndFlush(billingAddress);
        when(mockBillingAddressSearchRepository.search(queryStringQuery("id:" + billingAddress.getId())))
            .thenReturn(Collections.singletonList(billingAddress));

        // Search the billingAddress
        restBillingAddressMockMvc.perform(get("/api/_search/billing-addresses?query=id:" + billingAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(billingAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].alternativePhoneNumber").value(hasItem(DEFAULT_ALTERNATIVE_PHONE_NUMBER)));
    }
}
