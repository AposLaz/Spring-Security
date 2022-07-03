package com.aplaz.oauthresourceserver.repository;

import com.aplaz.oauthresourceserver.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
