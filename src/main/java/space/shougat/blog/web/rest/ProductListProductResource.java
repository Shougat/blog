package space.shougat.blog.web.rest;

import space.shougat.blog.domain.ProductListProduct;
import space.shougat.blog.repository.ProductListProductRepository;
import space.shougat.blog.repository.search.ProductListProductSearchRepository;
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
 * REST controller for managing {@link space.shougat.blog.domain.ProductListProduct}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProductListProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductListProductResource.class);

    private static final String ENTITY_NAME = "productListProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductListProductRepository productListProductRepository;

    private final ProductListProductSearchRepository productListProductSearchRepository;

    public ProductListProductResource(ProductListProductRepository productListProductRepository, ProductListProductSearchRepository productListProductSearchRepository) {
        this.productListProductRepository = productListProductRepository;
        this.productListProductSearchRepository = productListProductSearchRepository;
    }

    /**
     * {@code POST  /product-list-products} : Create a new productListProduct.
     *
     * @param productListProduct the productListProduct to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productListProduct, or with status {@code 400 (Bad Request)} if the productListProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-list-products")
    public ResponseEntity<ProductListProduct> createProductListProduct(@RequestBody ProductListProduct productListProduct) throws URISyntaxException {
        log.debug("REST request to save ProductListProduct : {}", productListProduct);
        if (productListProduct.getId() != null) {
            throw new BadRequestAlertException("A new productListProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductListProduct result = productListProductRepository.save(productListProduct);
        productListProductSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/product-list-products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-list-products} : Updates an existing productListProduct.
     *
     * @param productListProduct the productListProduct to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productListProduct,
     * or with status {@code 400 (Bad Request)} if the productListProduct is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productListProduct couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-list-products")
    public ResponseEntity<ProductListProduct> updateProductListProduct(@RequestBody ProductListProduct productListProduct) throws URISyntaxException {
        log.debug("REST request to update ProductListProduct : {}", productListProduct);
        if (productListProduct.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ProductListProduct result = productListProductRepository.save(productListProduct);
        productListProductSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productListProduct.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /product-list-products} : get all the productListProducts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productListProducts in body.
     */
    @GetMapping("/product-list-products")
    public List<ProductListProduct> getAllProductListProducts() {
        log.debug("REST request to get all ProductListProducts");
        return productListProductRepository.findAll();
    }

    /**
     * {@code GET  /product-list-products/:id} : get the "id" productListProduct.
     *
     * @param id the id of the productListProduct to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productListProduct, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-list-products/{id}")
    public ResponseEntity<ProductListProduct> getProductListProduct(@PathVariable Long id) {
        log.debug("REST request to get ProductListProduct : {}", id);
        Optional<ProductListProduct> productListProduct = productListProductRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(productListProduct);
    }

    /**
     * {@code DELETE  /product-list-products/:id} : delete the "id" productListProduct.
     *
     * @param id the id of the productListProduct to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-list-products/{id}")
    public ResponseEntity<Void> deleteProductListProduct(@PathVariable Long id) {
        log.debug("REST request to delete ProductListProduct : {}", id);
        productListProductRepository.deleteById(id);
        productListProductSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/product-list-products?query=:query} : search for the productListProduct corresponding
     * to the query.
     *
     * @param query the query of the productListProduct search.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-list-products")
    public List<ProductListProduct> searchProductListProducts(@RequestParam String query) {
        log.debug("REST request to search ProductListProducts for query {}", query);
        return StreamSupport
            .stream(productListProductSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
