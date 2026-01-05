package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.OrderProduct;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    @Query(
        """
            select op
            from OrderProduct op
            where op.order.id = :orderId
        """
    )
    List<OrderProduct> findByOrderId(@Param("orderId") Long orderId);
}
