package com.example.ictassetstracking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.ictassetstracking.dto.AssetDto;
import com.example.ictassetstracking.entity.Asset;
import com.example.ictassetstracking.entity.Category;
import com.example.ictassetstracking.entity.Employee;
import com.example.ictassetstracking.entity.MainCategory;
import com.example.ictassetstracking.entity.ReleasedAsset;
import com.example.ictassetstracking.repository.AssetRepository;
import com.example.ictassetstracking.repository.CategoryRepository;
import com.example.ictassetstracking.repository.EmployeeRepository;
import com.example.ictassetstracking.repository.MainCategoryRepository;
import com.example.ictassetstracking.repository.ReleasedAssetRepository;
import com.example.ictassetstracking.repository.UserRepository;

@Service
public class AssetServiceImpl implements AssetService {
    private static final Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository emploRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ReleasedAssetRepository releasedAssetRepository;

    public AssetServiceImpl(AssetRepository assetRepository, UserRepository userRepository, EmployeeRepository emploRepository,
                          MainCategoryRepository mainCategoryRepository, CategoryRepository categoryRepository,
                          ReleasedAssetRepository releasedAssetRepository) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.emploRepository = emploRepository;
        this.mainCategoryRepository = mainCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.releasedAssetRepository = releasedAssetRepository;
    }


    @Override
    public List<AssetDto> listAllAssets() {
        return assetRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<AssetDto> listAssetsWithPagination(Pageable pageable) {
        Page<Asset> assetPage = assetRepository.findAll(pageable);
        List<AssetDto> dtoList = assetPage.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, assetPage.getTotalElements());
    }

    @Override
    public Page<AssetDto> listUnreleasedAssetsWithPagination(Pageable pageable) {
        Page<Asset> assetPage = assetRepository.findByIsreleasedFalse(pageable);
        List<AssetDto> dtoList = assetPage.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, assetPage.getTotalElements());
    }

    @Override
    public AssetDto getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + id));
        return toDto(asset);
    }

    @Override
    public AssetDto createAsset(AssetDto dto) {
        logger.info("Creating asset: {}, checkNumber: {}", dto.getName(), dto.getCheckNumber());
        
        // Check for duplicate serial number and check number combination (only for Individual mode)
        String assignmentType = dto.getAssignmentType() != null ? dto.getAssignmentType() : "Individual";
        if (assignmentType.equalsIgnoreCase("Individual") && dto.getCheckNumber() != null) {
            if (assetRepository.existsBySerialNumberAndCheckNumber(dto.getSerialNumber(), dto.getCheckNumber())) {
                throw new IllegalArgumentException("An asset with serial number '" + dto.getSerialNumber() + "' and check number '" + dto.getCheckNumber() + "' already exists");
            }
        }
        
        Employee owner = null;
        Long checkNumber = dto.getCheckNumber();
        
        if (dto.getAssignmentType() == null || dto.getAssignmentType().equalsIgnoreCase("Individual")) {
            if (checkNumber != null) {
                try {
                    owner = emploRepository.findByCheckNumber(checkNumber).orElse(null);
                    if (owner != null) {
                        logger.info("Found employee with check number: {}", checkNumber);
                    } else {
                        logger.warn("No employee found with check number: {}, but asset will still be created", checkNumber);
                    }
                } catch (Exception e) {
                    logger.error("Error resolving owner: {}", e.getMessage());
                }
            }
        } else {
            // For Institution mode, use default check number
            checkNumber = 0L;
        }

        MainCategory mainCategory = null;
        if (dto.getMainCategoryId() != null) {
            mainCategory = mainCategoryRepository.findById(dto.getMainCategoryId()).orElse(null);
        }

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        }

        String departmentName = dto.getDepartmentName();
        if ((departmentName == null || departmentName.isBlank()) && owner != null) {
            departmentName = owner.getDepartmentName();
        }

        Asset asset = new Asset(
            category != null ? category.getName() : dto.getName(),
                dto.getSerialNumber(),
                dto.getDescription(),
                departmentName,
                checkNumber,
                owner
        );
        asset.setMainCategory(mainCategory);
        asset.setCategory(category);
        asset.setCreatedAt(LocalDateTime.now());
        asset.setTsnCode(dto.getTsnCode());

        Asset saved = assetRepository.save(asset);
        logger.info("Asset created with ID: {}, CheckNumber: {}, Owner: {}",
                saved.getId(), saved.getCheckNumber(), saved.getOwner() != null ? saved.getOwner().getUsername() : "null");

        return toDto(saved);
    }

    @Override
    public AssetDto updateAsset(Long id, AssetDto dto) {
        logger.info("Updating asset ID: {}", id);
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + id));
        
        // Check if serial number and check number combination already exists (only for Individual mode)
        String assignmentType = dto.getAssignmentType() != null ? dto.getAssignmentType() : "Individual";
        if (assignmentType.equalsIgnoreCase("Individual")) {
            if (!asset.getSerialNumber().equals(dto.getSerialNumber()) || !asset.getCheckNumber().equals(dto.getCheckNumber())) {
                if (assetRepository.existsBySerialNumberAndCheckNumber(dto.getSerialNumber(), dto.getCheckNumber())) {
                    throw new IllegalArgumentException("An asset with serial number '" + dto.getSerialNumber() + "' and check number '" + dto.getCheckNumber() + "' already exists");
                }
            }
        }

        MainCategory mainCategory = null;
        if (dto.getMainCategoryId() != null) {
            mainCategory = mainCategoryRepository.findById(dto.getMainCategoryId()).orElse(null);
        }
        asset.setMainCategory(mainCategory);

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        }
        asset.setCategory(category);

        Employee owner = null;
        Long checkNumber = dto.getCheckNumber();
        
        if (dto.getAssignmentType() == null || dto.getAssignmentType().equalsIgnoreCase("Individual")) {
            if (checkNumber != null) {
                try {
                    owner = emploRepository.findByCheckNumber(checkNumber).orElse(null);
                } catch (Exception e) {
                    logger.error("Error resolving owner during update: {}", e.getMessage());
                }
            }
        } else {
            // For Institution mode, use default check number
            checkNumber = 0L;
        }

        asset.setName(category != null ? category.getName() : dto.getName());
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setDescription(dto.getDescription());
        String departmentName = dto.getDepartmentName();
        if ((departmentName == null || departmentName.isBlank()) && owner != null) {
            departmentName = owner.getDepartmentName();
        }
        asset.setDepartmentName(departmentName);
        asset.setCheckNumber(checkNumber);
        asset.setTsnCode(dto.getTsnCode());
        asset.setOwner(owner);
        asset.setUpdatedAt(LocalDateTime.now());
        
        Asset saved = assetRepository.save(asset);
        logger.info("Asset updated. New owner: {}", owner != null ? owner.getUsername() : "null");
        return toDto(saved);
    }

    @Override
    public boolean deleteAsset(Long id) {
        if (!assetRepository.existsById(id)) {
            throw new IllegalArgumentException("Asset not found: " + id);
        }
        assetRepository.deleteById(id);
        return true;
    }

    @Override
    public AssetDto releaseAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + id));
        if (asset.isIsreleased()) {
            throw new IllegalArgumentException("Asset is already released: " + id);
        }

        String departmentName = asset.getDepartmentName();
        if ((departmentName == null || departmentName.isBlank()) && asset.getOwner() != null) {
            departmentName = asset.getOwner().getDepartmentName();
        }

        ReleasedAsset releasedAsset = new ReleasedAsset(
                asset.getName(),
                asset.getSerialNumber(),
                asset.getDescription(),
                departmentName,
                asset.getCheckNumber(),
                true
        );
        releasedAsset.setTsnCode(asset.getTsnCode());
        releasedAsset.setCreatedAt(LocalDateTime.now());
        releasedAsset.setUpdatedAt(LocalDateTime.now());

        ReleasedAsset savedReleasedAsset = releasedAssetRepository.save(releasedAsset);

        asset.setIsreleased(true);
        asset.setReleasedAsset(savedReleasedAsset);
        asset.setUpdatedAt(LocalDateTime.now());
        assetRepository.save(asset);

        return toDto(asset);
    }

    private Employee resolveOwner(Long checkNumber) {
        if (checkNumber == null) {
            logger.warn("Owner check number is null");
            return null;
        }

        logger.info("Resolving owner check number: {}", checkNumber);
        return emploRepository.findByCheckNumber(checkNumber)
                .orElseThrow(() -> {
                    logger.error("Employee not found with check number: {}", checkNumber);
                    return new IllegalArgumentException("Employee not found: " + checkNumber);
                });
    }

    @Override
    public java.util.List<AssetDto> listAssetsForUser(String username) {
        logger.info("listAssetsForUser called with username: {}", username);
        if (username == null || username.isEmpty()) {
            logger.warn("Username is null or empty");
            return java.util.Collections.emptyList();
        }
        
        // Try direct database query first (case-insensitive)
        java.util.List<Asset> assets = assetRepository.findByOwnerFullNameIgnoreCase(username);
        logger.info("Found {} assets for username: {}", assets.size(), username);
        
        // Fallback: if no assets found, try by Employee lookup
        if (assets.isEmpty()) {
            Employee employee = userToEmployee(username);
            if (employee != null) {
                logger.info("Found employee {} with id {}", employee.getUsername(), employee.getId());
                assets = assetRepository.findByOwner(employee);
                logger.info("Found {} assets via employee lookup", assets.size());
            } else {
                logger.warn("Employee not found for username: {}", username);
            }
        }
        
        return assets.stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public java.util.List<AssetDto> listAssetsByCheckNumber(Long checkNumber) {
        if (checkNumber == null) {
            logger.warn("listAssetsByCheckNumber called with null checkNumber");
            return java.util.Collections.emptyList();
        }
        logger.info("Listing assets by checkNumber: {}", checkNumber);
        java.util.List<Asset> foundAssets = assetRepository.findByCheckNumber(checkNumber);
        logger.info("Query returned {} assets for checkNumber: {}", foundAssets.size(), checkNumber);
        
        for (Asset asset : foundAssets) {
            logger.info("  - Asset ID: {}, Name: {}, CheckNumber: {}", asset.getId(), asset.getName(), asset.getCheckNumber());
        }
        
        return foundAssets.stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public java.util.List<AssetDto> listUnreleasedAssetsByCheckNumber(Long checkNumber) {
        if (checkNumber == null) {
            logger.warn("listUnreleasedAssetsByCheckNumber called with null checkNumber");
            return java.util.Collections.emptyList();
        }
        logger.info("Listing unreleased assets by checkNumber: {}", checkNumber);
        java.util.List<Asset> foundAssets = assetRepository.findByCheckNumberAndIsreleasedFalse(checkNumber);
        logger.info("Query returned {} unreleased assets for checkNumber: {}", foundAssets.size(), checkNumber);
        return foundAssets.stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public long countAssetsByCheckNumber(Long checkNumber) {
        if (checkNumber == null) {
            logger.warn("countAssetsByCheckNumber called with null checkNumber");
            return 0L;
        }
        long count = assetRepository.countByCheckNumber(checkNumber);
        logger.info("countAssetsByCheckNumber: Found {} assets for checkNumber: {}", count, checkNumber);
        return count;
    }

    @Override
    public long countUnreleasedAssetsByCheckNumber(Long checkNumber) {
        if (checkNumber == null) {
            logger.warn("countUnreleasedAssetsByCheckNumber called with null checkNumber");
            return 0L;
        }
        long count = assetRepository.countByCheckNumberAndIsreleasedFalse(checkNumber);
        logger.info("countUnreleasedAssetsByCheckNumber: Found {} unreleased assets for checkNumber: {}", count, checkNumber);
        return count;
    }

    @Override
    public AssetDto createAssetForUser(AssetDto dto, String username) {
        // Check for duplicate serial number and check number combination
        Employee owner = userToEmployee(username);
        Long checkNum = owner != null ? owner.getCheckNumber() : dto.getCheckNumber();
        if (assetRepository.existsBySerialNumberAndCheckNumber(dto.getSerialNumber(), checkNum)) {
            throw new IllegalArgumentException("An asset with serial number '" + dto.getSerialNumber() + "' and check number '" + checkNum + "' already exists");
        }
        
        owner = userToEmployee(username);

        MainCategory mainCategory = null;
        if (dto.getMainCategoryId() != null) {
            mainCategory = mainCategoryRepository.findById(dto.getMainCategoryId()).orElse(null);
        }

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        }

        String departmentName = owner != null ? owner.getDepartmentName() : dto.getDepartmentName();

        Asset asset = new Asset(
            category != null ? category.getName() : dto.getName(),
                dto.getSerialNumber(),
                dto.getDescription(),
                departmentName,
                owner != null ? owner.getCheckNumber() : dto.getCheckNumber(),
                owner
        );
        asset.setMainCategory(mainCategory);
        asset.setCategory(category);
        asset.setCreatedAt(LocalDateTime.now());
        asset.setTsnCode(dto.getTsnCode());

        Asset saved = assetRepository.save(asset);
        return toDto(saved);
    }

    @Override
    public AssetDto updateAssetForUser(Long id, AssetDto dto, String username) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + id));
        Employee owner = userToEmployee(username);
        Long checkNum = owner != null ? owner.getCheckNumber() : dto.getCheckNumber();
        
        // Check if serial number and check number combination already exists (for different assets)
        if (!asset.getSerialNumber().equals(dto.getSerialNumber()) || !asset.getCheckNumber().equals(checkNum)) {
            if (assetRepository.existsBySerialNumberAndCheckNumber(dto.getSerialNumber(), checkNum)) {
                throw new IllegalArgumentException("An asset with serial number '" + dto.getSerialNumber() + "' and check number '" + checkNum + "' already exists");
            }
        }
        if (asset.getOwner() == null || owner == null || !asset.getOwner().getId().equals(owner.getId())) {
            throw new IllegalArgumentException("Not authorized to update this asset");
        }

        MainCategory mainCategory = null;
        if (dto.getMainCategoryId() != null) {
            mainCategory = mainCategoryRepository.findById(dto.getMainCategoryId()).orElse(null);
        }
        asset.setMainCategory(mainCategory);

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        }
        asset.setCategory(category);

        asset.setName(category != null ? category.getName() : dto.getName());
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setDescription(dto.getDescription());
        asset.setDepartmentName(owner != null ? owner.getDepartmentName() : dto.getDepartmentName());
        asset.setCheckNumber(owner.getCheckNumber());
        asset.setTsnCode(dto.getTsnCode());
        asset.setOwner(owner);
        asset.setUpdatedAt(LocalDateTime.now());

        Asset saved = assetRepository.save(asset);
        return toDto(saved);
    }

    private Employee userToEmployee(String username) {
        if (username == null) return null;
        logger.debug("userToEmployee lookup for username: {}", username);
        // Try case-insensitive first
        java.util.Optional<Employee> emp = emploRepository.findByFullNameIgnoreCase(username);
        if (emp.isPresent()) {
            logger.debug("Found employee via case-insensitive lookup");
            return emp.get();
        }
        // Fallback to exact match
        emp = emploRepository.findByFullName(username);
        if (emp.isPresent()) {
            logger.debug("Found employee via exact match");
            return emp.get();
        }
        logger.debug("Employee not found for username: {}", username);
        return null;
    }

    private AssetDto toDto(Asset asset) {
        AssetDto dto = new AssetDto();
        dto.setId(asset.getId());
        dto.setName(asset.getName());
        dto.setSerialNumber(asset.getSerialNumber());
        dto.setDescription(asset.getDescription());
        dto.setDepartmentName(asset.getDepartmentName());
        dto.setCheckNumber(asset.getCheckNumber());
        dto.setTsnCode(asset.getTsnCode());
        dto.setAssignmentType(asset.getOwner() == null ? "Institution" : "Individual");
        if (asset.getOwner() != null) {
            dto.setOwnerUsername(asset.getOwner().getUsername());
            dto.setOwnerFirstName(asset.getOwner().getFirstName());
            dto.setOwnerLastName(asset.getOwner().getLastName());
        } else {
            dto.setOwnerUsername("Institution");
        }
        dto.setMainCategoryId(asset.getMainCategory() != null ? asset.getMainCategory().getId() : null);
        dto.setCategoryId(asset.getCategory() != null ? asset.getCategory().getId() : null);
        dto.setIsreleased(asset.isIsreleased());
        dto.setCreatedAt(asset.getCreatedAt());
        dto.setUpdatedAt(asset.getUpdatedAt());
        return dto;
    }
}
