package com.example.liskovbackend.repository;

import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
    Solution findByProperty(Property property);

    Optional<Solution> findByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);
}
