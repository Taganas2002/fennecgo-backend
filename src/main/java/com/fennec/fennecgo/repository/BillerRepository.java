package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.Biller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillerRepository extends JpaRepository<Biller, Long> {
    // Add custom query methods here if needed.
}
