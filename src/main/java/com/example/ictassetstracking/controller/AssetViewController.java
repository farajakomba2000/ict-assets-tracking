package com.example.ictassetstracking.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.bind.annotation.RequestParam;

import com.example.ictassetstracking.dto.AssetDto;
import com.example.ictassetstracking.entity.UserAccount;
import com.example.ictassetstracking.repository.MainCategoryRepository;
import com.example.ictassetstracking.repository.UserRepository;
import com.example.ictassetstracking.service.AssetService;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.stream.Collectors;

@Controller
public class AssetViewController {

    private final AssetService assetService;
    private final MainCategoryRepository mainCategoryRepository;
    private final UserRepository userRepository;

    public AssetViewController(AssetService assetService, MainCategoryRepository mainCategoryRepository, UserRepository userRepository) {
        this.assetService = assetService;
        this.mainCategoryRepository = mainCategoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/assets")
    public String listAssets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        Page<AssetDto> assetsPage = assetService.listUnreleasedAssetsWithPagination(pageable);
        
        model.addAttribute("assetsPage", assetsPage);
        model.addAttribute("assets", assetsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", assetsPage.getTotalPages());
        model.addAttribute("totalElements", assetsPage.getTotalElements());
        
        model.addAttribute("asset", new AssetDto());
        model.addAttribute("mainCategories", mainCategoryRepository.findAll());
        model.addAttribute("distinctDepartments", userRepository.findDistinctDepartmentNames());
        return "assets";
    }

    @GetMapping({"/assets/new", "/assets/register"})
    public String createForm(Model model, Principal principal) {
        AssetDto asset = new AssetDto();
        
        // Auto-populate check number from logged-in user
        if (principal != null) {
            try {
                Long checkNumber = Long.valueOf(principal.getName());
                UserAccount user = userRepository.findByCheckNumber(checkNumber).orElse(null);
                if (user != null) {
                    asset.setCheckNumber(checkNumber);
                }
            } catch (NumberFormatException e) {
                // Username is not numeric, skip
            }
        }
        
        model.addAttribute("asset", asset);
        model.addAttribute("mainCategories", mainCategoryRepository.findAll());
        model.addAttribute("distinctDepartments", userRepository.findDistinctDepartmentNames());
        return "asset-form";
    }

    @GetMapping("/assets/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        AssetDto dto = assetService.getAssetById(id);
        model.addAttribute("asset", dto);
        model.addAttribute("mainCategories", mainCategoryRepository.findAll());
        model.addAttribute("distinctDepartments", userRepository.findDistinctDepartmentNames());
        return "asset-form";
    }

    @GetMapping("/assets/release")
    public String releaseForm(Model model) {
        model.addAttribute("asset", new AssetDto());
        model.addAttribute("notReleasedAssets", assetService.listAllAssets().stream()
                .filter(asset -> !asset.isIsreleased())
                .collect(Collectors.toList()));
        return "release-form";
    }

    @PostMapping("/assets/release")
    public String releaseAsset(@ModelAttribute("asset") AssetDto assetDto, Model model) {
        try {
            if (assetDto.getId() == null) {
                model.addAttribute("errorMessage", "Please select an asset to release.");
                model.addAttribute("notReleasedAssets", assetService.listAllAssets().stream()
                        .filter(asset -> !asset.isIsreleased())
                        .collect(Collectors.toList()));
                return "release-form";
            }
            assetService.releaseAsset(assetDto.getId());
            return "redirect:/dashboard";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("notReleasedAssets", assetService.listAllAssets().stream()
                    .filter(asset -> !asset.isIsreleased())
                    .collect(Collectors.toList()));
            return "release-form";
        }
    }

    @GetMapping("/assets-client")
    public String assetsClient() {
        return "assets-client";
    }

    @GetMapping("/api/assets")
    @ResponseBody
    public java.util.List<AssetDto> listAssetsJson() {
        return assetService.listAllAssets();
    }

    @PostMapping("/assets")
    public String createAsset(@Valid @ModelAttribute("asset") AssetDto assetDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("mainCategories", mainCategoryRepository.findAll());
            return "asset-form";
        }
        try {
            assetService.createAsset(assetDto);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("mainCategories", mainCategoryRepository.findAll());
            model.addAttribute("asset", assetDto);
            return "asset-form";
        }
        return "redirect:/assets";
    }

    @PostMapping("/assets/{id}")
    public String updateAsset(@PathVariable Long id, @Valid @ModelAttribute("asset") AssetDto assetDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("mainCategories", mainCategoryRepository.findAll());
            return "asset-form";
        }
        try {
            assetDto.setId(id);
            assetService.updateAsset(id, assetDto);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("mainCategories", mainCategoryRepository.findAll());
            model.addAttribute("asset", assetDto);
            model.addAttribute("id", id);
            return "asset-form";
        }
        return "redirect:/assets";
    }

    @PostMapping("/assets/{id}/delete")
    public String deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return "redirect:/assets";
    }

    @GetMapping("/api/categories/{mainCategoryId}")
    @ResponseBody
    public java.util.List<com.example.ictassetstracking.entity.Category> getCategories(@PathVariable Long mainCategoryId) {
        var mainCategory = mainCategoryRepository.findById(mainCategoryId);
        if (mainCategory.isPresent()) {
            return new java.util.ArrayList<>(mainCategory.get().getCategories());
        }
        return java.util.Collections.emptyList();
    }

    @GetMapping("/api/users/search")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> searchUsers(@RequestParam String q) {
        java.util.List<java.util.Map<String, Object>> results = new java.util.ArrayList<>();
        if (q == null || q.trim().isEmpty()) {
            return results;
        }
        try {
            var users = userRepository.searchByName(q.trim());
            for (var user : users) {
                var map = new java.util.HashMap<String, Object>();
                map.put("id", user.getId());
                map.put("checkNumber", user.getCheckNumber());
                String firstName = user.getFirstName() != null ? user.getFirstName() : "";
                String lastName = user.getLastName() != null ? user.getLastName() : "";
                map.put("fullName", (firstName + " " + lastName).trim());
                map.put("departmentName", user.getDepartmentName() != null ? user.getDepartmentName() : "");
                results.add(map);
            }
        } catch (Exception e) {
            // Return empty list on error
        }
        return results;
    }
}
