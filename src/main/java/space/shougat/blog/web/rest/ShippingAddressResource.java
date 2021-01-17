package space.shougat.blog.web.rest;

import space.shougat.blog.domain.ShippingAddress;
import space.shougat.blog.repository.ShippingAddressRepository;
import space.shougat.blog.repository.search.ShippingAddressSearchRepository;
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
 * REST controller for managing {@link space.shougat.blog.domain.ShippingAddress}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ShippingAddressResource {

    private final Logger log = LoggerFactory.getLogger(ShippingAddressResource.class);

    private static final String ENTITY_NAME = "shippingAddress";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShippingAddressRepository shippingAddressRepository;

    private final ShippingAddressSearchRepository shippingAddressSearchRepository;

    public ShippingAddressResource(ShippingAddressRepository shippingAddressRepository, ShippingAddressSearchRepository shippingAddressSearchRepository) {
        this.shippingAddressRepository = shippingAddressRepository;
        this.shippingAddressSearchRepository = shippingAddressSearchRepository;
    }

    /**
     * {@code POST  /shipping-addresses} : Create a new shippingAddress.
     *
     * @param shippingAddress the shippingAddress to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shippingAddress, or with status {@code 400 (Bad Request)} if the shippingAddress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shipping-addresses")
    public ResponseEntity<ShippingAddress> createShippingAddress(@RequestBody ShippingAddress shippingAddress) throws URISyntaxException {
        log.debug("REST request to save ShippingAddress : {}", shippingAddress);
        if (shippingAddress.getId() != null) {
            throw new BadRequestAlertException("A new shippingAddress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ShippingAddress result = shippingAddressRepository.save(shippingAddress);
        shippingAddressSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/shipping-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /shipping-addresses} : Updates an existing shippingAddress.
     *
     * @param shippingAddress the shippingAddress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shippingAddress,
     * or with status {@code 400 (Bad Request)} if the shippingAddress is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shippingAddress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shipping-addresses")
    public ResponseEntity<ShippingAddress> updateShippingAddress(@RequestBody ShippingAddress shippingAddress) throws URISyntaxException {
        log.debug("REST request to update ShippingAddress : {}", shippingAddress);
        if (shippingAddress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ShippingAddress result = shippingAddressRepository.save(shippingAddress);
        shippingAddressSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, shippingAddress.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /shipping-addresses} : get all the shippingAddresses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shippingAddresses in body.
     */
    @GetMapping("/shipping-addresses")
    public List<ShippingAddress> getAllShippingAddresses() {
        log.debug("REST request to get all ShippingAddresses");
        return shippingAddressRepository.findAll();
    }

    /**
     * {@code GET  /shipping-addresses/:id} : get the "id" shippingAddress.
     *
     * @param id the id of the shippingAddress to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shippingAddress, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/shipping-addresses/{id}")
    public ResponseEntity<ShippingAddress> getShippingAddress(@PathVariable Long id) {
        log.debug("REST request to get ShippingAddress : {}", id);
        Optional<ShippingAddress> shippingAddress = shippingAddressRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(shippingAddress);
    }

    /**
     * {@code DELETE  /shipping-addresses/:id} : delete the "id" shippingAddress.
     *
     * @param id the id of the shippingAddress to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/shipping-addresses/{id}")
    public ResponseEntity<Void> deleteShippingAddress(@PathVariable Long id) {
        log.debug("REST request to delete ShippingAddress : {}", id);
        shippingAddressRepository.deleteById(id);
        shippingAddressSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/shipping-addresses?query=:query} : search for the shippingAddress corresponding
     * to the query.
     *
     * @param query the query of the shippingAddress search.
     * @return the result of the search.
     */
    @GetMapping("/_search/shipping-addresses")
    public List<ShippingAddress> searchShippingAddresses(@RequestParam String query) {
        log.debug("REST request to search ShippingAddresses for query {}", query);
        return StreamSupport
            .stream(shippingAddressSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
