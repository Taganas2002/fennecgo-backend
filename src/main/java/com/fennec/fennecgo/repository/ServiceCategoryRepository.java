package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    // Add custom queries if needed
}
