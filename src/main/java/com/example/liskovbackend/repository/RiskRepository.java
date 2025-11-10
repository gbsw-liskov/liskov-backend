package com.example.liskovbackend.repository;

import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.entity.Risk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiskRepository extends JpaRepository<Risk, Long> {
    Optional<Risk> findByProperty(Property property);
}
