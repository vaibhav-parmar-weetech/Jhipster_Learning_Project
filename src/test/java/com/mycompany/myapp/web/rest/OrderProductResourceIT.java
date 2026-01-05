package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.OrderProductAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.OrderProduct;
import com.mycompany.myapp.repository.OrderProductRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrderProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderProductResourceIT {

    private static final Integer DEFAULT_ORDER_QTY = 1;
    private static final Integer UPDATED_ORDER_QTY = 2;

    private static final String ENTITY_API_URL = "/api/order-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderProductMockMvc;

    private OrderProduct orderProduct;

    private OrderProduct insertedOrderProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderProduct createEntity() {
        return new OrderProduct().orderQty(DEFAULT_ORDER_QTY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderProduct createUpdatedEntity() {
        return new OrderProduct().orderQty(UPDATED_ORDER_QTY);
    }

    @BeforeEach
    void initTest() {
        orderProduct = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrderProduct != null) {
            orderProductRepository.delete(insertedOrderProduct);
            insertedOrderProduct = null;
        }
    }

    @Test
    @Transactional
    void createOrderProduct() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OrderProduct
        var returnedOrderProduct = om.readValue(
            restOrderProductMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderProduct)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrderProduct.class
        );

        // Validate the OrderProduct in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertOrderProductUpdatableFieldsEquals(returnedOrderProduct, getPersistedOrderProduct(returnedOrderProduct));

        insertedOrderProduct = returnedOrderProduct;
    }

    @Test
    @Transactional
    void createOrderProductWithExistingId() throws Exception {
        // Create the OrderProduct with an existing ID
        orderProduct.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderProduct)))
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOrderProducts() throws Exception {
        // Initialize the database
        insertedOrderProduct = orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList
        restOrderProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderQty").value(hasItem(DEFAULT_ORDER_QTY)));
    }

    @Test
    @Transactional
    void getOrderProduct() throws Exception {
        // Initialize the database
        insertedOrderProduct = orderProductRepository.saveAndFlush(orderProduct);

        // Get the orderProduct
        restOrderProductMockMvc
            .perform(get(ENTITY_API_URL_ID, orderProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderProduct.getId().intValue()))
            .andExpect(jsonPath("$.orderQty").value(DEFAULT_ORDER_QTY));
    }

    @Test
    @Transactional
    void getNonExistingOrderProduct() throws Exception {
        // Get the orderProduct
        restOrderProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderProduct() throws Exception {
        // Initialize the database
        insertedOrderProduct = orderProductRepository.saveAndFlush(orderProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderProduct
        OrderProduct updatedOrderProduct = orderProductRepository.findById(orderProduct.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrderProduct are not directly saved in db
        em.detach(updatedOrderProduct);
        updatedOrderProduct.orderQty(UPDATED_ORDER_QTY);

        restOrderProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrderProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedOrderProduct))
            )
            .andExpect(status().isOk());

        // Validate the OrderProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderProductToMatchAllProperties(updatedOrderProduct);
    }

    @Test
    @Transactional
    void putNonExistingOrderProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderProduct.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderProduct.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderProduct.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderProduct)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderProductWithPatch() throws Exception {
        // Initialize the database
        insertedOrderProduct = orderProductRepository.saveAndFlush(orderProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderProduct using partial update
        OrderProduct partialUpdatedOrderProduct = new OrderProduct();
        partialUpdatedOrderProduct.setId(orderProduct.getId());

        restOrderProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderProduct))
            )
            .andExpect(status().isOk());

        // Validate the OrderProduct in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderProductUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderProduct, orderProduct),
            getPersistedOrderProduct(orderProduct)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrderProductWithPatch() throws Exception {
        // Initialize the database
        insertedOrderProduct = orderProductRepository.saveAndFlush(orderProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderProduct using partial update
        OrderProduct partialUpdatedOrderProduct = new OrderProduct();
        partialUpdatedOrderProduct.setId(orderProduct.getId());

        partialUpdatedOrderProduct.orderQty(UPDATED_ORDER_QTY);

        restOrderProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderProduct))
            )
            .andExpect(status().isOk());

        // Validate the OrderProduct in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderProductUpdatableFieldsEquals(partialUpdatedOrderProduct, getPersistedOrderProduct(partialUpdatedOrderProduct));
    }

    @Test
    @Transactional
    void patchNonExistingOrderProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderProduct.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderProduct.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderProduct.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orderProduct)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderProduct() throws Exception {
        // Initialize the database
        insertedOrderProduct = orderProductRepository.saveAndFlush(orderProduct);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderProduct
        restOrderProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderProduct.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderProductRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected OrderProduct getPersistedOrderProduct(OrderProduct orderProduct) {
        return orderProductRepository.findById(orderProduct.getId()).orElseThrow();
    }

    protected void assertPersistedOrderProductToMatchAllProperties(OrderProduct expectedOrderProduct) {
        assertOrderProductAllPropertiesEquals(expectedOrderProduct, getPersistedOrderProduct(expectedOrderProduct));
    }

    protected void assertPersistedOrderProductToMatchUpdatableProperties(OrderProduct expectedOrderProduct) {
        assertOrderProductAllUpdatablePropertiesEquals(expectedOrderProduct, getPersistedOrderProduct(expectedOrderProduct));
    }
}
