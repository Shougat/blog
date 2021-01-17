package space.shougat.blog.web.rest;

import space.shougat.blog.domain.ProductList;
import space.shougat.blog.repository.ProductListRepository;
import space.shougat.blog.repository.search.ProductListSearchRepository;
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
 * REST controller for managing {@link space.shougat.blog.domain.ProductList}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProductListResource {

    private final Logger log = LoggerFactory.getLogger(ProductListResource.class);

    private static final String ENTITY_NAME = "productList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductListRepository productListRepository;

    private final ProductListSearchRepository productListSearchRepository;

    public ProductListResource(ProductListRepository productListRepository, ProductListSearchRepository productListSearchRepository) {
        this.productListRepository = productListRepository;
        this.productListSearchRepository = productListSearchRepository;
    }

    /**
     * {@code POST  /product-lists} : Create a new productList.
     *
     * @param productList the productList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productList, or with status {@code 400 (Bad Request)} if the productList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-lists")
    public ResponseEntity<ProductList> createProductList(@RequestBody ProductList productList) throws URISyntaxException {
        log.debug("REST request to save ProductList : {}", productList);
        if (productList.getId() != null) {
            throw new BadRequestAlertException("A new productList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductList result = productListRepository.save(productList);
        productListSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/product-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-lists} : Updates an existing productList.
     *
     * @param productList the productList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productList,
     * or with status {@code 400 (Bad Request)} if the productList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-lists")
    public ResponseEntity<ProductList> updateProductList(@RequestBody ProductList productList) throws URISyntaxException {
        log.debug("REST request to update ProductList : {}", productList);
        if (productList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ProductList result = productListRepository.save(productList);
        productListSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productList.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /product-lists} : get all the productLists.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productLists in body.
     */
    @GetMapping("/product-lists")
    public List<ProductList> getAllProductLists() {
        log.debug("REST request to get all ProductLists");
        return productListRepository.findAll();
    }

    /**
     * {@code GET  /product-lists/:id} : get the "id" productList.
     *
     * @param id the id of the productList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-lists/{id}")
    public ResponseEntity<ProductList> getProductList(@PathVariable Long id) {
        log.debug("REST request to get ProductList : {}", id);
        Optional<ProductList> productList = productListRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(productList);
    }

    /**
     * {@code DELETE  /product-lists/:id} : delete the "id" productList.
     *
     * @param id the id of the productList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-lists/{id}")
    public ResponseEntity<Void> deleteProductList(@PathVariable Long id) {
        log.debug("REST request to delete ProductList : {}", id);
        productListRepository.deleteById(id);
        productListSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/product-lists?query=:query} : search for the productList corresponding
     * to the query.
     *
     * @param query the query of the productList search.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-lists")
    public List<ProductList> searchProductLists(@RequestParam String query) {
        log.debug("REST request to search ProductLists for query {}", query);
        return StreamSupport
            .stream(productListSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
