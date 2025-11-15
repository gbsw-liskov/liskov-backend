package com.example.liskovbackend.repository;

import com.example.liskovbackend.entity.Analysis;
import com.example.liskovbackend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Optional<Analysis> findByProperty(Property property);
}
