package com.example.liskovbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("isDeleted = false")
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 256)
    private String password;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Property> properties;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Checklist> checklists;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Analysis> analyses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Solution> solutions;

    @Builder.Default
    private boolean isDeleted = false;

    public void updateUserInfo(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAnalyze(Analysis analysis) {
        this.analyses.add(analysis);
    }
}

