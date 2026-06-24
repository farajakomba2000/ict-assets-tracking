package com.example.ictassetstracking.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ictassetstracking.dto.AssetDto;

public interface AssetService {

    List<AssetDto> listAllAssets();

    Page<AssetDto> listAssetsWithPagination(Pageable pageable);

    Page<AssetDto> listUnreleasedAssetsWithPagination(Pageable pageable);

    AssetDto getAssetById(Long id);

    AssetDto createAsset(AssetDto dto);

    AssetDto updateAsset(Long id, AssetDto dto);

    AssetDto releaseAsset(Long id);

    boolean deleteAsset(Long id);

    java.util.List<AssetDto> listAssetsForUser(String username);

    java.util.List<AssetDto> listAssetsByCheckNumber(Long checkNumber);

    java.util.List<AssetDto> listUnreleasedAssetsByCheckNumber(Long checkNumber);

    long countAssetsByCheckNumber(Long checkNumber);

    long countUnreleasedAssetsByCheckNumber(Long checkNumber);

    AssetDto createAssetForUser(AssetDto dto, String username);

    AssetDto updateAssetForUser(Long id, AssetDto dto, String username);
}
