package space.shougat.blog.web.rest;

import space.shougat.blog.domain.OrderProduct;
import space.shougat.blog.repository.OrderProductRepository;
import space.shougat.blog.repository.search.OrderProductSearchRepository;
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
 * REST controller for managing {@link space.shougat.blog.domain.OrderProduct}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class OrderProductResource {

    private final Logger log = LoggerFactory.getLogger(OrderProductResource.class);

    private static final String ENTITY_NAME = "orderProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderProductRepository orderProductRepository;

    private final OrderProductSearchRepository orderProductSearchRepository;

    public OrderProductResource(OrderProductRepository orderProductRepository, OrderProductSearchRepository orderProductSearchRepository) {
        this.orderProductRepository = orderProductRepository;
        this.orderProductSearchRepository = orderProductSearchRepository;
    }

    /**
     * {@code POST  /order-products} : Create a new orderProduct.
     *
     * @param orderProduct the orderProduct to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderProduct, or with status {@code 400 (Bad Request)} if the orderProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-products")
    public ResponseEntity<OrderProduct> createOrderProduct(@RequestBody OrderProduct orderProduct) throws URISyntaxException {
        log.debug("REST request to save OrderProduct : {}", orderProduct);
        if (orderProduct.getId() != null) {
            throw new BadRequestAlertException("A new orderProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderProduct result = orderProductRepository.save(orderProduct);
        orderProductSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/order-products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-products} : Updates an existing orderProduct.
     *
     * @param orderProduct the orderProduct to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderProduct,
     * or with status {@code 400 (Bad Request)} if the orderProduct is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderProduct couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-products")
    public ResponseEntity<OrderProduct> updateOrderProduct(@RequestBody OrderProduct orderProduct) throws URISyntaxException {
        log.debug("REST request to update OrderProduct : {}", orderProduct);
        if (orderProduct.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        OrderProduct result = orderProductRepository.save(orderProduct);
        orderProductSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderProduct.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /order-products} : get all the orderProducts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderProducts in body.
     */
    @GetMapping("/order-products")
    public List<OrderProduct> getAllOrderProducts() {
        log.debug("REST request to get all OrderProducts");
        return orderProductRepository.findAll();
    }

    /**
     * {@code GET  /order-products/:id} : get the "id" orderProduct.
     *
     * @param id the id of the orderProduct to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderProduct, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-products/{id}")
    public ResponseEntity<OrderProduct> getOrderProduct(@PathVariable Long id) {
        log.debug("REST request to get OrderProduct : {}", id);
        Optional<OrderProduct> orderProduct = orderProductRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(orderProduct);
    }

    /**
     * {@code DELETE  /order-products/:id} : delete the "id" orderProduct.
     *
     * @param id the id of the orderProduct to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-products/{id}")
    public ResponseEntity<Void> deleteOrderProduct(@PathVariable Long id) {
        log.debug("REST request to delete OrderProduct : {}", id);
        orderProductRepository.deleteById(id);
        orderProductSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/order-products?query=:query} : search for the orderProduct corresponding
     * to the query.
     *
     * @param query the query of the orderProduct search.
     * @return the result of the search.
     */
    @GetMapping("/_search/order-products")
    public List<OrderProduct> searchOrderProducts(@RequestParam String query) {
        log.debug("REST request to search OrderProducts for query {}", query);
        return StreamSupport
            .stream(orderProductSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
