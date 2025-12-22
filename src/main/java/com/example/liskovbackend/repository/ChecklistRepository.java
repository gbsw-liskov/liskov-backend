package com.example.liskovbackend.repository;

import com.example.liskovbackend.entity.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    Optional<Checklist> findByIdAndIsDeletedFalse(Long id);

    Optional<Checklist> findByPropertyId(Long propertyId);
}
