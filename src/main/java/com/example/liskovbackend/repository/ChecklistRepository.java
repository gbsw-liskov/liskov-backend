package com.example.liskovbackend.repository;

import com.example.liskovbackend.entity.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    List<Checklist> findAllByUserId(Long userId);

    Optional<Checklist> findByIdAndUserId(Long id, Long userId);
}
