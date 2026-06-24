package com.example.ictassetstracking.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.ictassetstracking.dto.ReleasedAssetDto;
import com.example.ictassetstracking.service.ReleasedAssetService;

@Controller
public class HandoverDashboardController {

    private final ReleasedAssetService releasedAssetService;

    public HandoverDashboardController(ReleasedAssetService releasedAssetService) {
        this.releasedAssetService = releasedAssetService;
    }

    @GetMapping("/handover")
    public String handoverDashboard(Model model) {
        return "handover-dashboard";
    }

    @GetMapping("/reports/released-assets")
    public String releasedAssetsReport(Model model) {
        List<ReleasedAssetDto> releasedAssets = releasedAssetService.listAllReleasedAssets();
        model.addAttribute("releasedAssets", releasedAssets);
        return "released-assets-report";
    }
}
