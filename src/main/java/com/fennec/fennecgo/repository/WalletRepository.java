package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
	List<Wallet> findByUserId(Long userId);
    Optional<Wallet> findByUser_Id(Long userId);
    Optional<Wallet> findDefaultWalletByUserId(Long userId);
    
}
