package com.example.liskovbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;

@Entity
@Table(name = "analysis_details")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;

    @Column(nullable = false)
    private String original;

    @Column(columnDefinition = "TEXT")
    private String analysisText;

    @Enumerated(EnumType.STRING)
    private Severity severity;
}

