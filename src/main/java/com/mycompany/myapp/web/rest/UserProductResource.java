package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.ProductService;
import com.mycompany.myapp.service.dto.ProductDTO;
import com.mycompany.myapp.service.dto.UserProductResponseDTO;
import java.util.List;
import java.util.Optional;
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
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/user/products")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class UserProductResource {

    @Autowired
    ProductService productService;

    private static final Logger LOG = LoggerFactory.getLogger(UserProductResource.class);

    @GetMapping
    public ResponseEntity<List<UserProductResponseDTO>> getAllProducts(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Products");
        Page<UserProductResponseDTO> page = productService.findAllProductForUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProductResponseDTO> getProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Product : {}", id);
        Optional<UserProductResponseDTO> productDTO = productService.findOneForUser(id);
        return ResponseUtil.wrapOrNotFound(productDTO);
    }
}
