package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.BillerRequest;
import com.fennec.fennecgo.dto.response.BillerResponse;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.Biller;
import com.fennec.fennecgo.repository.BillerRepository;
import com.fennec.fennecgo.services.Interface.BillerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillerServiceImpl implements BillerService {

    private final BillerRepository billerRepository;

    @Override
    public BillerResponse createBiller(BillerRequest request) {
        Biller biller = Biller.builder()
                .externalCode(request.getExternalCode())
                .name(request.getName())
                .category(request.getCategory())
                .logoUrl(request.getLogoUrl())
                .description(request.getDescription())
                .build();

        billerRepository.save(biller);
        return toResponse(biller);
    }

    @Override
    public List<BillerResponse> getAllBillers() {
        return billerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BillerResponse getBillerById(Long id) {
        Biller biller = billerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Biller not found with id: " + id));
        return toResponse(biller);
    }

    @Override
    public BillerResponse updateBiller(Long id, BillerRequest request) {
        Biller biller = billerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Biller not found with id: " + id));

        // Update fields
        biller.setExternalCode(request.getExternalCode());
        biller.setName(request.getName());
        biller.setCategory(request.getCategory());
        biller.setLogoUrl(request.getLogoUrl());
        biller.setDescription(request.getDescription());

        billerRepository.save(biller);
        return toResponse(biller);
    }

    @Override
    public void deleteBiller(Long id) {
        if (!billerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Biller not found with id: " + id);
        }
        billerRepository.deleteById(id);
    }

    private BillerResponse toResponse(Biller biller) {
        BillerResponse response = new BillerResponse();
        response.setId(biller.getId());
        response.setExternalCode(biller.getExternalCode());
        response.setName(biller.getName());
        response.setCategory(biller.getCategory());
        response.setLogoUrl(biller.getLogoUrl());
        response.setDescription(biller.getDescription());
        return response;
    }
}
