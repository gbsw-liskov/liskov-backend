package com.example.liskovbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "risks")
public class Risk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(nullable = false)
    private Integer totalRisk;

    private String comment;

    @OneToMany(mappedBy = "risk", cascade = CascadeType.ALL)
    private List<Detail> details;

    @OneToMany(mappedBy = "risk", cascade = CascadeType.ALL)
    private List<Coping> coping;

    private List<String> checklist;
}
