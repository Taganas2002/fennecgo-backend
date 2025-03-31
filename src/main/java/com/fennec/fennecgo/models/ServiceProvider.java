package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique external code for the provider
    @Column(nullable = false, unique = true)
    private String externalCode;

    // Name of the provider (e.g., "Fortnite", "Sonelgaz")
    @Column(nullable = false)
    private String name;

    // Reference to the ServiceCategory
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_category_id", nullable = false)
    private ServiceCategory category;

    private String logoUrl;
    private String description;
}