package com.example.liskovbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("is_deleted = false")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;

    @Column(nullable = false)
    private Integer floor;

    @Column(name = "built_year", nullable = false)
    private Integer builtYear;

    // 면적(단위: 평)
    @Column(precision = 5, scale = 2)
    private BigDecimal area;

    // 시세
    @Column(name = "market_price", nullable = false)
    private Integer marketPrice;

    // 임대 종류
    @Column(name = "lease_type", nullable = false)
    private LeaseType leaseType;

    // 보증금(=전세)
    @Column(nullable = false)
    private Integer deposit;

    // 월세, 전세일 경우 0
    @Column(name = "monthly_rent")
    private Integer monthlyRent;

    @Column(nullable = false)
    private String memo;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Analysis analysis;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Solution solution;

    public void delete() {
        this.isDeleted = true;
    }
}

