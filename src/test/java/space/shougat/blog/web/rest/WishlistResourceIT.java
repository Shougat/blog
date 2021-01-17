package space.shougat.blog.web.rest;

import space.shougat.blog.BlogApp;
import space.shougat.blog.domain.Wishlist;
import space.shougat.blog.repository.WishlistRepository;
import space.shougat.blog.repository.search.WishlistSearchRepository;

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
 * Integration tests for the {@link WishlistResource} REST controller.
 */
@SpringBootTest(classes = BlogApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class WishlistResourceIT {

    @Autowired
    private WishlistRepository wishlistRepository;

    /**
     * This repository is mocked in the space.shougat.blog.repository.search test package.
     *
     * @see space.shougat.blog.repository.search.WishlistSearchRepositoryMockConfiguration
     */
    @Autowired
    private WishlistSearchRepository mockWishlistSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWishlistMockMvc;

    private Wishlist wishlist;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wishlist createEntity(EntityManager em) {
        Wishlist wishlist = new Wishlist();
        return wishlist;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wishlist createUpdatedEntity(EntityManager em) {
        Wishlist wishlist = new Wishlist();
        return wishlist;
    }

    @BeforeEach
    public void initTest() {
        wishlist = createEntity(em);
    }

    @Test
    @Transactional
    public void createWishlist() throws Exception {
        int databaseSizeBeforeCreate = wishlistRepository.findAll().size();
        // Create the Wishlist
        restWishlistMockMvc.perform(post("/api/wishlists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wishlist)))
            .andExpect(status().isCreated());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeCreate + 1);
        Wishlist testWishlist = wishlistList.get(wishlistList.size() - 1);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(1)).save(testWishlist);
    }

    @Test
    @Transactional
    public void createWishlistWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = wishlistRepository.findAll().size();

        // Create the Wishlist with an existing ID
        wishlist.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWishlistMockMvc.perform(post("/api/wishlists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wishlist)))
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeCreate);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(0)).save(wishlist);
    }


    @Test
    @Transactional
    public void getAllWishlists() throws Exception {
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);

        // Get all the wishlistList
        restWishlistMockMvc.perform(get("/api/wishlists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishlist.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getWishlist() throws Exception {
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);

        // Get the wishlist
        restWishlistMockMvc.perform(get("/api/wishlists/{id}", wishlist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wishlist.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingWishlist() throws Exception {
        // Get the wishlist
        restWishlistMockMvc.perform(get("/api/wishlists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWishlist() throws Exception {
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);

        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();

        // Update the wishlist
        Wishlist updatedWishlist = wishlistRepository.findById(wishlist.getId()).get();
        // Disconnect from session so that the updates on updatedWishlist are not directly saved in db
        em.detach(updatedWishlist);

        restWishlistMockMvc.perform(put("/api/wishlists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedWishlist)))
            .andExpect(status().isOk());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);
        Wishlist testWishlist = wishlistList.get(wishlistList.size() - 1);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(1)).save(testWishlist);
    }

    @Test
    @Transactional
    public void updateNonExistingWishlist() throws Exception {
        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishlistMockMvc.perform(put("/api/wishlists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wishlist)))
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(0)).save(wishlist);
    }

    @Test
    @Transactional
    public void deleteWishlist() throws Exception {
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);

        int databaseSizeBeforeDelete = wishlistRepository.findAll().size();

        // Delete the wishlist
        restWishlistMockMvc.perform(delete("/api/wishlists/{id}", wishlist.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(1)).deleteById(wishlist.getId());
    }

    @Test
    @Transactional
    public void searchWishlist() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);
        when(mockWishlistSearchRepository.search(queryStringQuery("id:" + wishlist.getId())))
            .thenReturn(Collections.singletonList(wishlist));

        // Search the wishlist
        restWishlistMockMvc.perform(get("/api/_search/wishlists?query=id:" + wishlist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishlist.getId().intValue())));
    }
}
