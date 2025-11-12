package com.example.liskovbackend.repository;

import com.example.liskovbackend.entity.Risk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskRepository extends JpaRepository<Risk, Long> {
}
