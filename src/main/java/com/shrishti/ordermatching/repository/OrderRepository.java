package com.shrishti.ordermatching.repository;

import com.shrishti.ordermatching.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findBySymbol(String symbol);
}
