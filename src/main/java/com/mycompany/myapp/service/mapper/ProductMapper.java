package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Order;
import com.mycompany.myapp.domain.Product;
import com.mycompany.myapp.service.dto.OrderDTO;
import com.mycompany.myapp.service.dto.ProductDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "orders", source = "orders", qualifiedByName = "orderIdSet")
    ProductDTO toDto(Product s);

    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "removeOrders", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    @Named("orderIdSet")
    default Set<OrderDTO> toDtoOrderIdSet(Set<Order> order) {
        return order.stream().map(this::toDtoOrderId).collect(Collectors.toSet());
    }
}
