package com.example.ictassetstracking.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "released_asset", uniqueConstraints = @jakarta.persistence.UniqueConstraint(columnNames = {"serialNumber", "check_number"}))
public class ReleasedAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private String serialNumber;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "check_number", nullable = false)
    private Long checkNumber;

    @Column(name = "tsn_code", nullable = true)
    private String tsnCode;
    @OneToMany(mappedBy = "releasedAsset", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asset> assets = new ArrayList<>();
    @Column(nullable = true)
    private boolean reassigned = false;

    @Column(name = "created_at", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    public ReleasedAsset() {
    }

    public ReleasedAsset(String name, String serialNumber, String description, String departmentName,
            Long checkNumber, boolean reassigned) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.description = description;
        this.departmentName = departmentName;
        this.checkNumber = checkNumber;
        this.reassigned = reassigned;
    }

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

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
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
}
