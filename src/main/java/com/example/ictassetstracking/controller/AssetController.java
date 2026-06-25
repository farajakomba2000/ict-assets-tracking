package com.example.ictassetstracking.controller;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.example.ictassetstracking.dto.AssetDto;
import com.example.ictassetstracking.dto.AssetInput;
import com.example.ictassetstracking.service.AssetService;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;



@GraphQLApi
@Component
public class AssetController {
    private static Logger logger = LoggerFactory.getLogger(AssetController.class);


    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    
    @GraphQLQuery(name = "assets")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    public List<AssetDto> assets() {
        return assetService.listAllAssets();
    }

    @GraphQLQuery(name = "asset")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    public AssetDto asset(@GraphQLArgument(name = "id") Long id) {
        return assetService.getAssetById(id);
    }

    @GraphQLMutation(name = "createAsset")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public AssetDto createAsset(@GraphQLArgument(name = "asset") AssetInput input) {
        AssetDto dto = toDto(input);
        return assetService.createAsset(dto);
    }

    @GraphQLMutation(name = "updateAsset")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public AssetDto updateAsset(@GraphQLArgument(name = "id") Long id,
                                @GraphQLArgument(name = "asset") AssetInput input) 
                                {
         logger.info("Updating asset with id {}: {}", id, input.getName());                          

        AssetDto dto = toDto(input);
        return assetService.updateAsset(id, dto);
    }

    @GraphQLMutation(name = "deleteAsset")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteAsset(@GraphQLArgument(name = "id") Long id) {
        return assetService.deleteAsset(id);
    }

    private AssetDto toDto(AssetInput input) {
        AssetDto dto = new AssetDto();
        dto.setName(input.getName());
        dto.setSerialNumber(input.getSerialNumber());
        dto.setDescription(input.getDescription());
        dto.setDepartmentName(input.getDepartmentName());
        dto.setOwnerUsername(input.getOwnerUsername());
        dto.setMainCategoryId(input.getMainCategoryId());
        dto.setCategoryId(input.getCategoryId());
        return dto;
    }
}
