package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.MoneyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {
    List<MoneyRequest> findByToWalletID_Id(Long walletId);
    List<MoneyRequest> findByFromWalletID_Id(Long walletId);
    List<MoneyRequest> findByFromWalletID_IdIn(List<Long> walletIds);

}
