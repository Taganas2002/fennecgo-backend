package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "billers")
public class Biller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A unique external code for referencing the biller externally.
     * Example: "ELEC_COMPANY", "WATER_CO", "INTERNET_PROVIDER"
     */
    @Column(nullable = false, unique = true)
    private String externalCode;

    /**
     * The displayed name.
     * Example: "Electricity Company", "Water Utility", "Internet Provider"
     */
    @Column(nullable = false)
    private String name;

    /**
     * Category or grouping for this biller.
     * Example: "UTILITY", "TELECOM", "INSURANCE"
     */
    private String category;

    /**
     * Optional URL for the biller's logo or icon.
     */
    private String logoUrl;

    /**
     * Additional description or notes.
     */
    @Column(length = 1000)
    private String description;
}
