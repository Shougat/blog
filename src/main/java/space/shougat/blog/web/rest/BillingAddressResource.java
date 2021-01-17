package space.shougat.blog.web.rest;

import space.shougat.blog.domain.BillingAddress;
import space.shougat.blog.repository.BillingAddressRepository;
import space.shougat.blog.repository.search.BillingAddressSearchRepository;
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
 * REST controller for managing {@link space.shougat.blog.domain.BillingAddress}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BillingAddressResource {

    private final Logger log = LoggerFactory.getLogger(BillingAddressResource.class);

    private static final String ENTITY_NAME = "billingAddress";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BillingAddressRepository billingAddressRepository;

    private final BillingAddressSearchRepository billingAddressSearchRepository;

    public BillingAddressResource(BillingAddressRepository billingAddressRepository, BillingAddressSearchRepository billingAddressSearchRepository) {
        this.billingAddressRepository = billingAddressRepository;
        this.billingAddressSearchRepository = billingAddressSearchRepository;
    }

    /**
     * {@code POST  /billing-addresses} : Create a new billingAddress.
     *
     * @param billingAddress the billingAddress to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new billingAddress, or with status {@code 400 (Bad Request)} if the billingAddress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/billing-addresses")
    public ResponseEntity<BillingAddress> createBillingAddress(@RequestBody BillingAddress billingAddress) throws URISyntaxException {
        log.debug("REST request to save BillingAddress : {}", billingAddress);
        if (billingAddress.getId() != null) {
            throw new BadRequestAlertException("A new billingAddress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BillingAddress result = billingAddressRepository.save(billingAddress);
        billingAddressSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/billing-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /billing-addresses} : Updates an existing billingAddress.
     *
     * @param billingAddress the billingAddress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billingAddress,
     * or with status {@code 400 (Bad Request)} if the billingAddress is not valid,
     * or with status {@code 500 (Internal Server Error)} if the billingAddress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/billing-addresses")
    public ResponseEntity<BillingAddress> updateBillingAddress(@RequestBody BillingAddress billingAddress) throws URISyntaxException {
        log.debug("REST request to update BillingAddress : {}", billingAddress);
        if (billingAddress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BillingAddress result = billingAddressRepository.save(billingAddress);
        billingAddressSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, billingAddress.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /billing-addresses} : get all the billingAddresses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of billingAddresses in body.
     */
    @GetMapping("/billing-addresses")
    public List<BillingAddress> getAllBillingAddresses() {
        log.debug("REST request to get all BillingAddresses");
        return billingAddressRepository.findAll();
    }

    /**
     * {@code GET  /billing-addresses/:id} : get the "id" billingAddress.
     *
     * @param id the id of the billingAddress to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the billingAddress, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/billing-addresses/{id}")
    public ResponseEntity<BillingAddress> getBillingAddress(@PathVariable Long id) {
        log.debug("REST request to get BillingAddress : {}", id);
        Optional<BillingAddress> billingAddress = billingAddressRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(billingAddress);
    }

    /**
     * {@code DELETE  /billing-addresses/:id} : delete the "id" billingAddress.
     *
     * @param id the id of the billingAddress to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/billing-addresses/{id}")
    public ResponseEntity<Void> deleteBillingAddress(@PathVariable Long id) {
        log.debug("REST request to delete BillingAddress : {}", id);
        billingAddressRepository.deleteById(id);
        billingAddressSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/billing-addresses?query=:query} : search for the billingAddress corresponding
     * to the query.
     *
     * @param query the query of the billingAddress search.
     * @return the result of the search.
     */
    @GetMapping("/_search/billing-addresses")
    public List<BillingAddress> searchBillingAddresses(@RequestParam String query) {
        log.debug("REST request to search BillingAddresses for query {}", query);
        return StreamSupport
            .stream(billingAddressSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
