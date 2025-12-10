package com.example.liskovbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
@SQLRestriction("isDeleted = false")
@SQLDelete(sql = "UPDATE properties SET deleted = true WHERE id = ?")
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

    @Column(name = "build_year")
    private Integer buildYear;

    @Column(precision = 5, scale = 2)
    private BigDecimal area;

    @Column(name = "available_date")
    private LocalDate availableDate;

    @Column(name = "market_price")
    private Integer marketPrice;

    private Integer deposit;

    @Column(name = "monthly_rent")
    private Integer monthlyRent;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "property", cascade = CascadeType.PERSIST)
    private List<Checklist> checklists;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Analysis analysis;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Solution solution;

    @Builder.Default
    private boolean isDeleted = false;
}

