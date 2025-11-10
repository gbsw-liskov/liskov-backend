package com.example.liskovbackend.repository;

import com.example.liskovbackend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
