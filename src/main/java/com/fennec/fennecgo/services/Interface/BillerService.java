package com.fennec.fennecgo.services.Interface;

import java.util.List;

import com.fennec.fennecgo.dto.request.BillerRequest;
import com.fennec.fennecgo.dto.response.BillerResponse;

public interface BillerService {
	BillerResponse createBiller(BillerRequest request);
    List<BillerResponse> getAllBillers();
    BillerResponse getBillerById(Long id);
    BillerResponse updateBiller(Long id, BillerRequest request);
    void deleteBiller(Long id);
}
