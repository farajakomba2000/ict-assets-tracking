package com.example.ictassetstracking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ictassetstracking.entity.Asset;
import com.example.ictassetstracking.entity.Employee;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
	java.util.List<Asset> findByOwner(Employee owner);

    java.util.List<Asset> findByCheckNumber(Long checkNumber);

    java.util.List<Asset> findByCheckNumberAndIsreleasedFalse(Long checkNumber);

    org.springframework.data.domain.Page<Asset> findByIsreleasedFalse(Pageable pageable);

    long countByCheckNumber(Long checkNumber);

    long countByCheckNumberAndIsreleasedFalse(Long checkNumber);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM Asset a JOIN a.owner e WHERE LOWER(e.fullName) = LOWER(?1)")
    java.util.List<Asset> findByOwnerFullNameIgnoreCase(String fullName);
    
    boolean existsBySerialNumberAndCheckNumber(String serialNumber, Long checkNumber);
}
