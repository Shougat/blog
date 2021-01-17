package space.shougat.blog.web.rest;

import space.shougat.blog.domain.Wishlist;
import space.shougat.blog.repository.WishlistRepository;
import space.shougat.blog.repository.search.WishlistSearchRepository;
import space.shougat.blog.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link space.shougat.blog.domain.Wishlist}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class WishlistResource {

    private final Logger log = LoggerFactory.getLogger(WishlistResource.class);

    private static final String ENTITY_NAME = "wishlist";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WishlistRepository wishlistRepository;

    private final WishlistSearchRepository wishlistSearchRepository;

    public WishlistResource(WishlistRepository wishlistRepository, WishlistSearchRepository wishlistSearchRepository) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistSearchRepository = wishlistSearchRepository;
    }

    /**
     * {@code POST  /wishlists} : Create a new wishlist.
     *
     * @param wishlist the wishlist to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wishlist, or with status {@code 400 (Bad Request)} if the wishlist has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wishlists")
    public ResponseEntity<Wishlist> createWishlist(@RequestBody Wishlist wishlist) throws URISyntaxException {
        log.debug("REST request to save Wishlist : {}", wishlist);
        if (wishlist.getId() != null) {
            throw new BadRequestAlertException("A new wishlist cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Wishlist result = wishlistRepository.save(wishlist);
        wishlistSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/wishlists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /wishlists} : Updates an existing wishlist.
     *
     * @param wishlist the wishlist to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishlist,
     * or with status {@code 400 (Bad Request)} if the wishlist is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wishlist couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wishlists")
    public ResponseEntity<Wishlist> updateWishlist(@RequestBody Wishlist wishlist) throws URISyntaxException {
        log.debug("REST request to update Wishlist : {}", wishlist);
        if (wishlist.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Wishlist result = wishlistRepository.save(wishlist);
        wishlistSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, wishlist.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /wishlists} : get all the wishlists.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wishlists in body.
     */
    @GetMapping("/wishlists")
    public List<Wishlist> getAllWishlists() {
        log.debug("REST request to get all Wishlists");
        return wishlistRepository.findAll();
    }

    /**
     * {@code GET  /wishlists/:id} : get the "id" wishlist.
     *
     * @param id the id of the wishlist to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wishlist, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/wishlists/{id}")
    public ResponseEntity<Wishlist> getWishlist(@PathVariable Long id) {
        log.debug("REST request to get Wishlist : {}", id);
        Optional<Wishlist> wishlist = wishlistRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(wishlist);
    }

    /**
     * {@code DELETE  /wishlists/:id} : delete the "id" wishlist.
     *
     * @param id the id of the wishlist to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/wishlists/{id}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Long id) {
        log.debug("REST request to delete Wishlist : {}", id);
        wishlistRepository.deleteById(id);
        wishlistSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/wishlists?query=:query} : search for the wishlist corresponding
     * to the query.
     *
     * @param query the query of the wishlist search.
     * @return the result of the search.
     */
    @GetMapping("/_search/wishlists")
    public List<Wishlist> searchWishlists(@RequestParam String query) {
        log.debug("REST request to search Wishlists for query {}", query);
        return StreamSupport
            .stream(wishlistSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
