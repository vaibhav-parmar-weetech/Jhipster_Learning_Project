package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.ProductRepository;
import com.mycompany.myapp.service.dto.UserProductResponseDTO;
import com.mycompany.myapp.service.mapper.ProductMapper;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductExtendedService extends ProductServiceImpl {

    public ProductExtendedService(ProductRepository productRepository, ProductMapper productMapper) {
        super(productRepository, productMapper);
    }

    @Override
    public Page<UserProductResponseDTO> findAllProductForUser(Pageable pageable) {
        return productRepository
            .findAll(pageable)
            .map(product -> {
                UserProductResponseDTO dto = new UserProductResponseDTO();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setPrice(product.getPrice());
                dto.setQty(product.getQty());
                return dto;
            });
    }

    @Override
    public Optional<UserProductResponseDTO> findOneForUser(Long id) {
        return productRepository
            .findById(id)
            .map(product -> {
                UserProductResponseDTO dto = new UserProductResponseDTO();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setPrice(product.getPrice());
                dto.setQty(product.getQty());
                return dto;
            });
    }
}
