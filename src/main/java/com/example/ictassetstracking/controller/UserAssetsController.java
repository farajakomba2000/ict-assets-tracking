package com.example.ictassetstracking.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ictassetstracking.dto.AssetDto;
import com.example.ictassetstracking.entity.UserAccount;
import com.example.ictassetstracking.repository.CategoryRepository;
import com.example.ictassetstracking.repository.MainCategoryRepository;
import com.example.ictassetstracking.repository.UserRepository;
import com.example.ictassetstracking.service.AssetService;

@Controller
public class UserAssetsController {

    private static final Logger logger = LoggerFactory.getLogger(UserAssetsController.class);
    
    private final AssetService assetService;
    private final MainCategoryRepository mainCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public UserAssetsController(AssetService assetService, MainCategoryRepository mainCategoryRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.assetService = assetService;
        this.mainCategoryRepository = mainCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my-assets")
    public String myAssets(Model model, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        model.addAttribute("assets", assetService.listAssetsForUser(username));
        return "my-assets";
    }

    @GetMapping("/my-assets/new")
    public String createForm(Model model) {
        model.addAttribute("asset", new AssetDto());
        model.addAttribute("mainCategories", mainCategoryRepository.findAll());
        return "my-asset-form";
    }

    @GetMapping("/my-assets/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        AssetDto dto = assetService.getAssetById(id);
        model.addAttribute("asset", dto);
        model.addAttribute("id", id);
        model.addAttribute("mainCategories", mainCategoryRepository.findAll());
        return "my-asset-form";
    }

    @PostMapping("/my-assets")
    public String createAsset(AssetDto assetDto, Model model, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        try {
            assetService.createAssetForUser(assetDto, username);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("asset", assetDto);
            model.addAttribute("mainCategories", mainCategoryRepository.findAll());
            return "my-asset-form";
        }
        return "redirect:/my-assets";
    }

    @PostMapping("/my-assets/{id}")
    public String updateAsset(@PathVariable Long id, AssetDto assetDto, Model model, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        try {
            assetService.updateAssetForUser(id, assetDto, username);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("asset", assetDto);
            model.addAttribute("id", id);
            model.addAttribute("mainCategories", mainCategoryRepository.findAll());
            return "my-asset-form";
        }
        return "redirect:/my-assets";
    }

    @PostMapping("/my-assets/{id}/delete")
    public String deleteAsset(@PathVariable Long id, Principal principal) {
        // allow delete only if owned by user
        assetService.deleteAsset(id);
        return "redirect:/my-assets";
    }

    @GetMapping("/my-assigned-assets-report")
    public String myAssignedAssetsReport(Model model, Principal principal) {
        // Get current user's check number
        String checkNumberStr = principal != null ? principal.getName() : null;
        
        logger.info("myAssignedAssetsReport: checkNumberStr = {}", checkNumberStr);
        
        if (checkNumberStr == null || checkNumberStr.isBlank()) {
            logger.warn("Check number is null or blank");
            return "redirect:/dashboard";
        }
        
        try {
            Long checkNumber = Long.valueOf(checkNumberStr);
            logger.info("myAssignedAssetsReport: Parsed checkNumber = {}", checkNumber);
            
            // Get user information
            UserAccount user = userRepository.findByCheckNumber(checkNumber)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with check number: " + checkNumber));
            
            logger.info("myAssignedAssetsReport: Found user with first name: {}", user.getFirstName());
            
            // Get only unreleased assets for this user by check number
            var assets = assetService.listUnreleasedAssetsByCheckNumber(checkNumber);
            long assetCount = assetService.countUnreleasedAssetsByCheckNumber(checkNumber);
            
            logger.info("myAssignedAssetsReport: Found {} unreleased assets for check number {}", assetCount, checkNumber);
            
            // Build user display name (firstName lastName (checkNumber))
            String userDisplayName = (user.getFirstName() != null ? user.getFirstName() : "User") + 
                    (user.getFirstName() != null && user.getFirstName().length() > 0 ? " " : "") +
                    "(" + checkNumber + ")";
            
            model.addAttribute("assets", assets);
            model.addAttribute("assetCount", assetCount);
            model.addAttribute("userDisplayName", userDisplayName);
            model.addAttribute("userName", user.getFirstName());
            model.addAttribute("checkNumber", checkNumber);
            
            return "my-assigned-assets-report";
        } catch (NumberFormatException e) {
            logger.error("Failed to parse check number: {}", checkNumberStr);
            return "redirect:/dashboard";
        }
    }
}
