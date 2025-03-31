package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique code for the category, e.g., "GAMES", "UTILITIES", etc.
    @Column(nullable = false, unique = true)
    private String code;

    // Display name for the category
    @Column(nullable = false)
    private String name;

    // Optionally add an icon URL or any additional fields
    private String iconUrl;
}

