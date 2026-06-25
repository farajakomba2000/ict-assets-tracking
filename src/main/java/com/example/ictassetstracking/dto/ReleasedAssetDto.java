package com.example.ictassetstracking.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReleasedAssetDto {
    private Long id;
    private String name;
    private String description;
    private String serialNumber;
    private String departmentName;
    private Long checkNumber;
    private String tsnCode;
    private boolean reassigned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int assetCount;
    private List<String> assetNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(Long checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getTsnCode() {
        return tsnCode;
    }

    public void setTsnCode(String tsnCode) {
        this.tsnCode = tsnCode;
    }

    public boolean isReassigned() {
        return reassigned;
    }

    public void setReassigned(boolean reassigned) {
        this.reassigned = reassigned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getAssetCount() {
        return assetCount;
    }

    public void setAssetCount(int assetCount) {
        this.assetCount = assetCount;
    }

    public List<String> getAssetNames() {
        return assetNames;
    }

    public void setAssetNames(List<String> assetNames) {
        this.assetNames = assetNames;
    }
}
