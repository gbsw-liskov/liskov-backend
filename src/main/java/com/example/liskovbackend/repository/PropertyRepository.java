package com.example.liskovbackend.repository;

import com.example.liskovbackend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // userId 기준 활성 매물 전체 조회
    @Query("SELECT p FROM Property p WHERE p.user.id = :userId AND p.isDeleted = false")
    List<Property> findAllActiveByUserId(Long userId);

    // userId 기준 특정 매물 조회
    @Query("SELECT p FROM Property p WHERE p.id = :id AND p.user.id = :userId AND p.isDeleted = false")
    Optional<Property> findByIdAndUserId(Long id, Long userId);

}