package com.shrishti.ordermatching.repository;

import com.shrishti.ordermatching.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<TradeEntity, Long> {
    List<TradeEntity> findBySymbol(String symbol);
}
