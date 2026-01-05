package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Order;
import com.mycompany.myapp.domain.OrderProduct;
import com.mycompany.myapp.domain.Product;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.OrderProductRepository;
import com.mycompany.myapp.repository.OrderRepository;
import com.mycompany.myapp.repository.ProductRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.OrderService;
import com.mycompany.myapp.service.dto.CreateOrderDTO;
import com.mycompany.myapp.service.dto.OrderDTO;
import com.mycompany.myapp.service.dto.OrderProductDTO;
import com.mycompany.myapp.service.dto.UserOrderResponseDTO;
import com.mycompany.myapp.service.mapper.OrderMapper;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Order}.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderProductRepository orderProductRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderDTO save(OrderDTO orderDTO) {
        LOG.debug("Request to save Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDTO update(OrderDTO orderDTO) {
        LOG.debug("Request to update Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public Optional<OrderDTO> partialUpdate(OrderDTO orderDTO) {
        LOG.debug("Request to partially update Order : {}", orderDTO);

        return orderRepository
            .findById(orderDTO.getId())
            .map(existingOrder -> {
                orderMapper.partialUpdate(existingOrder, orderDTO);

                return existingOrder;
            })
            .map(orderRepository::save)
            .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Orders");
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    public Page<OrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return orderRepository.findAllWithEagerRelationships(pageable).map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findOne(Long id) {
        LOG.debug("Request to get Order : {}", id);
        return orderRepository.findOneWithEagerRelationships(id).map(orderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderDTO dto) {
        // 1️⃣ Get logged-in user from JWT
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not authenticated"));

        User user = userRepository.findOneByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING"); // always pending
        order.setUser(user);

        Set<OrderProduct> orderProducts = new HashSet<>();
        double totalAmount = 0.0;

        for (OrderProductDTO p : dto.getProducts()) {
            Product product = productRepository.findById(p.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));

            if (p.getQty() == null || p.getQty() <= 0) {
                throw new RuntimeException("Quantity must be greater than zero");
            }

            if (product.getQty() < p.getQty()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            product.setQty(product.getQty() - p.getQty());

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setOrderQty(p.getQty());
            OrderProduct save = orderProductRepository.save(orderProduct);
            orderProducts.add(save);

            // 6️⃣ Calculate total
            totalAmount += product.getPrice() * p.getQty();
        }

        // 7️⃣ Finalize order
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserOrderResponseDTO> findAllForUser(Pageable pageable, String status) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not logged in"));

        Page<Order> orders;
        if (status != null && !status.isBlank()) {
            orders = orderRepository.findByUserLoginAndStatus(login, status, pageable);
        } else {
            orders = orderRepository.findByUserLogin(login, pageable);
        }

        return orders.map(order -> {
            UserOrderResponseDTO dto = new UserOrderResponseDTO();
            dto.setId(order.getId());
            dto.setOrderDate(order.getOrderDate());
            dto.setStatus(order.getStatus());
            dto.setTotalAmount(order.getTotalAmount());

            // ✅ Fetch ordered qty from join table
            Set<OrderProductDTO> productDTOs = orderProductRepository
                .findByOrderId(order.getId())
                .stream()
                .map(op -> {
                    OrderProductDTO pdto = new OrderProductDTO();
                    pdto.setProductId(op.getProduct().getId());
                    pdto.setQty(op.getOrderQty());
                    return pdto;
                })
                .collect(Collectors.toSet());

            dto.setProducts(productDTOs);
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UserOrderResponseDTO findOrderById(Long id) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not logged in"));

        Order order = orderRepository
            .findByIdAndUserLogin(id, login)
            .orElseThrow(() -> new RuntimeException("Order not found or access denied"));

        UserOrderResponseDTO dto = new UserOrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());

        // Map ordered products
        Set<OrderProductDTO> productDTOs = orderProductRepository
            .findByOrderId(order.getId())
            .stream()
            .map(op -> {
                OrderProductDTO pdto = new OrderProductDTO();
                pdto.setProductId(op.getProduct().getId());
                pdto.setQty(op.getOrderQty());
                return pdto;
            })
            .collect(Collectors.toSet());

        dto.setProducts(productDTOs);
        return dto;
    }

    @Override
    public void cancelOrder(Long id) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not logged in"));

        Order order = orderRepository.findByIdAndUserLogin(id, login).orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
}
