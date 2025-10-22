package com.example.liskovbackend.repository;

import com.example.liskovbackend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    @Query("SELECT p FROM Property p WHERE p.isDeleted = false")
    List<Property> findAllActive();
}
