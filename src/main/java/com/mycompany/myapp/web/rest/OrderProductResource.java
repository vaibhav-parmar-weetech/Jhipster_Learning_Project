package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.OrderProduct;
import com.mycompany.myapp.repository.OrderProductRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.OrderProduct}.
 */
@RestController
@RequestMapping("/api/order-products")
@Transactional
public class OrderProductResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrderProductResource.class);

    private static final String ENTITY_NAME = "orderProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderProductRepository orderProductRepository;

    public OrderProductResource(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    /**
     * {@code POST  /order-products} : Create a new orderProduct.
     *
     * @param orderProduct the orderProduct to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderProduct, or with status {@code 400 (Bad Request)} if the orderProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrderProduct> createOrderProduct(@RequestBody OrderProduct orderProduct) throws URISyntaxException {
        LOG.debug("REST request to save OrderProduct : {}", orderProduct);
        if (orderProduct.getId() != null) {
            throw new BadRequestAlertException("A new orderProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        orderProduct = orderProductRepository.save(orderProduct);
        return ResponseEntity.created(new URI("/api/order-products/" + orderProduct.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, orderProduct.getId().toString()))
            .body(orderProduct);
    }

    /**
     * {@code PUT  /order-products/:id} : Updates an existing orderProduct.
     *
     * @param id the id of the orderProduct to save.
     * @param orderProduct the orderProduct to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderProduct,
     * or with status {@code 400 (Bad Request)} if the orderProduct is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderProduct couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderProduct> updateOrderProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderProduct orderProduct
    ) throws URISyntaxException {
        LOG.debug("REST request to update OrderProduct : {}, {}", id, orderProduct);
        if (orderProduct.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderProduct.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        orderProduct = orderProductRepository.save(orderProduct);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderProduct.getId().toString()))
            .body(orderProduct);
    }

    /**
     * {@code PATCH  /order-products/:id} : Partial updates given fields of an existing orderProduct, field will ignore if it is null
     *
     * @param id the id of the orderProduct to save.
     * @param orderProduct the orderProduct to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderProduct,
     * or with status {@code 400 (Bad Request)} if the orderProduct is not valid,
     * or with status {@code 404 (Not Found)} if the orderProduct is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderProduct couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderProduct> partialUpdateOrderProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderProduct orderProduct
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OrderProduct partially : {}, {}", id, orderProduct);
        if (orderProduct.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderProduct.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderProduct> result = orderProductRepository
            .findById(orderProduct.getId())
            .map(existingOrderProduct -> {
                if (orderProduct.getOrderQty() != null) {
                    existingOrderProduct.setOrderQty(orderProduct.getOrderQty());
                }

                return existingOrderProduct;
            })
            .map(orderProductRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderProduct.getId().toString())
        );
    }

    /**
     * {@code GET  /order-products} : get all the orderProducts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderProducts in body.
     */
    @GetMapping("")
    public List<OrderProduct> getAllOrderProducts() {
        LOG.debug("REST request to get all OrderProducts");
        return orderProductRepository.findAll();
    }

    /**
     * {@code GET  /order-products/:id} : get the "id" orderProduct.
     *
     * @param id the id of the orderProduct to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderProduct, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderProduct> getOrderProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OrderProduct : {}", id);
        Optional<OrderProduct> orderProduct = orderProductRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(orderProduct);
    }

    /**
     * {@code DELETE  /order-products/:id} : delete the "id" orderProduct.
     *
     * @param id the id of the orderProduct to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OrderProduct : {}", id);
        orderProductRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
