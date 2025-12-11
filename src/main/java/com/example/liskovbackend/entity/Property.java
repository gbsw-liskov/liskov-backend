package com.example.liskovbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;
    
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;

    private Integer floor;

    @Column(name = "built_year")
    private Integer builtYear;

    // 면적(단위: 평)
    @Column(precision = 5, scale = 2)
    private BigDecimal area;

    // 시세
    @Column(name = "market_price")
    private Integer marketPrice;

    // 임대 종류
    private LeaseType leaseType;

    // 보증금(=전세)
    private Integer deposit;

    // 월세, 전세일 경우 0
    @Column(name = "monthly_rent")
    private Integer monthlyRent;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "property", cascade = CascadeType.PERSIST)
    private List<Checklist> checklists;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Analysis analysis;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Solution solution;

    @PrePersist
    public void prePersist() {
        isDeleted = false;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
    }
}

