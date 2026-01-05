package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.OrderRepository;
import com.mycompany.myapp.service.OrderService;
import com.mycompany.myapp.service.dto.CreateOrderDTO;
import com.mycompany.myapp.service.dto.OrderDTO;
import com.mycompany.myapp.service.dto.UserOrderResponseDTO;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/user/order")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class UserOrderResource {

    @Autowired
    OrderService orderService;

    private static final Logger LOG = LoggerFactory.getLogger(UserOrderResource.class);

    @PostMapping("create")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderDTO dto) {
        OrderDTO result = orderService.createOrder(dto);
        return new ResponseEntity<>(Map.of("Status", 201, "message", "Order Created Successfully.."), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserOrderResponseDTO>> getAllOrders(
        @ParameterObject Pageable pageable,
        @RequestParam(required = false) String status
    ) {
        Page<UserOrderResponseDTO> page = orderService.findAllForUser(pageable, status);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOrderResponseDTO> getOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get a {id} Orders");
        UserOrderResponseDTO userOrderResponseDTO = orderService.findOrderById(id);
        return ResponseEntity.ok(userOrderResponseDTO);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
