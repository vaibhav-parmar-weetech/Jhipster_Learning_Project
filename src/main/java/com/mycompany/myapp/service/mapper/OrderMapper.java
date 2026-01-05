package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Order;
import com.mycompany.myapp.domain.Product;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.OrderDTO;
import com.mycompany.myapp.service.dto.ProductDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "products", source = "products", qualifiedByName = "productIdSet")
    OrderDTO toDto(Order s);

    @Mapping(target = "removeProducts", ignore = true)
    Order toEntity(OrderDTO orderDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    @Named("productIdSet")
    default Set<ProductDTO> toDtoProductIdSet(Set<Product> product) {
        return product.stream().map(this::toDtoProductId).collect(Collectors.toSet());
    }
}
