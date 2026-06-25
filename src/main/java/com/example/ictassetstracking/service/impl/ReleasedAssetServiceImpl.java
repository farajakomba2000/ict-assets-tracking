package com.example.ictassetstracking.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.ictassetstracking.dto.ReleasedAssetDto;
import com.example.ictassetstracking.entity.ReleasedAsset;
import com.example.ictassetstracking.repository.ReleasedAssetRepository;
import com.example.ictassetstracking.service.ReleasedAssetService;

@Service
public class ReleasedAssetServiceImpl implements ReleasedAssetService {
    private final ReleasedAssetRepository releasedAssetRepository;

    public ReleasedAssetServiceImpl(ReleasedAssetRepository releasedAssetRepository) {
        this.releasedAssetRepository = releasedAssetRepository;
    }

    @Override
    public List<ReleasedAssetDto> listAllReleasedAssets() {
        return releasedAssetRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ReleasedAssetDto toDto(ReleasedAsset releasedAsset) {
        ReleasedAssetDto dto = new ReleasedAssetDto();
        dto.setId(releasedAsset.getId());
        dto.setName(releasedAsset.getName());
        dto.setDescription(releasedAsset.getDescription());
        dto.setSerialNumber(releasedAsset.getSerialNumber());
        dto.setDepartmentName(releasedAsset.getDepartmentName());
        dto.setCheckNumber(releasedAsset.getCheckNumber());
        dto.setTsnCode(releasedAsset.getTsnCode());
        dto.setReassigned(releasedAsset.isReassigned());
        dto.setCreatedAt(releasedAsset.getCreatedAt());
        dto.setUpdatedAt(releasedAsset.getUpdatedAt());
        dto.setAssetCount(releasedAsset.getAssets() != null ? releasedAsset.getAssets().size() : 0);
        dto.setAssetNames(releasedAsset.getAssets() == null ? java.util.Collections.emptyList() : releasedAsset.getAssets().stream()
                .map(a -> a.getName() + " (" + a.getSerialNumber() + ")")
                .collect(Collectors.toList()));
        return dto;
    }
}
