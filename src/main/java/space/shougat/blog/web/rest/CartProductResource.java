package space.shougat.blog.web.rest;

import space.shougat.blog.domain.CartProduct;
import space.shougat.blog.repository.CartProductRepository;
import space.shougat.blog.repository.search.CartProductSearchRepository;
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
 * REST controller for managing {@link space.shougat.blog.domain.CartProduct}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CartProductResource {

    private final Logger log = LoggerFactory.getLogger(CartProductResource.class);

    private static final String ENTITY_NAME = "cartProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartProductRepository cartProductRepository;

    private final CartProductSearchRepository cartProductSearchRepository;

    public CartProductResource(CartProductRepository cartProductRepository, CartProductSearchRepository cartProductSearchRepository) {
        this.cartProductRepository = cartProductRepository;
        this.cartProductSearchRepository = cartProductSearchRepository;
    }

    /**
     * {@code POST  /cart-products} : Create a new cartProduct.
     *
     * @param cartProduct the cartProduct to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cartProduct, or with status {@code 400 (Bad Request)} if the cartProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cart-products")
    public ResponseEntity<CartProduct> createCartProduct(@RequestBody CartProduct cartProduct) throws URISyntaxException {
        log.debug("REST request to save CartProduct : {}", cartProduct);
        if (cartProduct.getId() != null) {
            throw new BadRequestAlertException("A new cartProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CartProduct result = cartProductRepository.save(cartProduct);
        cartProductSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/cart-products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cart-products} : Updates an existing cartProduct.
     *
     * @param cartProduct the cartProduct to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartProduct,
     * or with status {@code 400 (Bad Request)} if the cartProduct is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cartProduct couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cart-products")
    public ResponseEntity<CartProduct> updateCartProduct(@RequestBody CartProduct cartProduct) throws URISyntaxException {
        log.debug("REST request to update CartProduct : {}", cartProduct);
        if (cartProduct.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CartProduct result = cartProductRepository.save(cartProduct);
        cartProductSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cartProduct.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /cart-products} : get all the cartProducts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cartProducts in body.
     */
    @GetMapping("/cart-products")
    public List<CartProduct> getAllCartProducts() {
        log.debug("REST request to get all CartProducts");
        return cartProductRepository.findAll();
    }

    /**
     * {@code GET  /cart-products/:id} : get the "id" cartProduct.
     *
     * @param id the id of the cartProduct to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cartProduct, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cart-products/{id}")
    public ResponseEntity<CartProduct> getCartProduct(@PathVariable Long id) {
        log.debug("REST request to get CartProduct : {}", id);
        Optional<CartProduct> cartProduct = cartProductRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cartProduct);
    }

    /**
     * {@code DELETE  /cart-products/:id} : delete the "id" cartProduct.
     *
     * @param id the id of the cartProduct to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cart-products/{id}")
    public ResponseEntity<Void> deleteCartProduct(@PathVariable Long id) {
        log.debug("REST request to delete CartProduct : {}", id);
        cartProductRepository.deleteById(id);
        cartProductSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/cart-products?query=:query} : search for the cartProduct corresponding
     * to the query.
     *
     * @param query the query of the cartProduct search.
     * @return the result of the search.
     */
    @GetMapping("/_search/cart-products")
    public List<CartProduct> searchCartProducts(@RequestParam String query) {
        log.debug("REST request to search CartProducts for query {}", query);
        return StreamSupport
            .stream(cartProductSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
