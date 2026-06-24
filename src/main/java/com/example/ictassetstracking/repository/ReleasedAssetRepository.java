package com.example.ictassetstracking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ictassetstracking.entity.ReleasedAsset;

@Repository
public interface ReleasedAssetRepository extends JpaRepository<ReleasedAsset, Long> {
    Optional<ReleasedAsset> findBySerialNumberAndCheckNumber(String serialNumber, Long checkNumber);
}
