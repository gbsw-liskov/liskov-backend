package com.example.liskovbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "solutions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("isDeleted = false")
@SQLDelete(sql = "UPDATE solutions SET deleted = true WHERE id = ?")
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolutionCoping> copings;

    private List<String> checklist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    private boolean isDeleted = false;


    public void updateCopings(List<SolutionCoping> savedSolutionCopings) {
        this.copings = savedSolutionCopings;
    }
}
