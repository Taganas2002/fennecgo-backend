package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    // Add custom query methods here if needed.
}
