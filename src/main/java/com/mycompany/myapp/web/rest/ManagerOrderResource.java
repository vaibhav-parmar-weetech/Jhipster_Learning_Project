package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Order;
import com.mycompany.myapp.service.OrderService;
import com.mycompany.myapp.service.dto.OrderDTO;
import com.mycompany.myapp.service.dto.UserOrderResponseDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/manager/order")
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
public class ManagerOrderResource {

    @Autowired
    OrderService orderService;

    private static final Logger LOG = LoggerFactory.getLogger(ManagerOrderResource.class);

    @GetMapping("")
    public ResponseEntity<List<UserOrderResponseDTO>> getAllOrders(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Orders");
        Page<UserOrderResponseDTO> page;

        page = orderService.findAllForManager(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Order> acceptOrder(@PathVariable Long id) {
        Order order = orderService.acceptOrder(id);
        return ResponseEntity.ok(order);
    }
}
