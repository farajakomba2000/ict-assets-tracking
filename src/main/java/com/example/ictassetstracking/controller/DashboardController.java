package com.example.ictassetstracking.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.ictassetstracking.dto.UserDto;
import com.example.ictassetstracking.service.AssetService;
import com.example.ictassetstracking.service.UserService;

@Controller
public class DashboardController {

    private final AssetService assetService;
    private final UserService userService;

    public DashboardController(AssetService assetService, UserService userService) {
        this.assetService = assetService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        boolean isUser = isUser();
        boolean noRole = hasNoRole();

        model.addAttribute("showUserManagement", isAdmin());
        model.addAttribute("showAssetRegistration", isAdmin() || isManager());
        model.addAttribute("showAssetHandover", isAdmin() || isManager());
        model.addAttribute("showMyAssignedAssets", isAdmin() || isManager() || isUser || noRole);
        model.addAttribute("showUserAssetAssignedReport", isAdmin() || isManager());
        model.addAttribute("showPersonalAssetCount", isUser || noRole);
        model.addAttribute("showGeneralSummary", !isUser && !noRole);

        if (model.containsAttribute("showGeneralSummary") && (boolean) model.getAttribute("showGeneralSummary")) {
            model.addAttribute("userCount", userService.listAllUsers().size());
            model.addAttribute("assetCount", assetService.listAllAssets().size());
            // Total assets assigned to users (have owner username)
            long assignedTotal = assetService.listAllAssets().stream()
                    .filter(a -> a.getOwnerUsername() != null && !a.getOwnerUsername().isBlank())
                    .count();
            model.addAttribute("assignedAssetsTotal", assignedTotal);
        } else {
            model.addAttribute("myAssetCount", countCurrentUserAssets());
        }

        return "dashboard";
    }

    private int countCurrentUserAssets() {
        String principalName = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        if (principalName == null || principalName.isBlank()) {
            return 0;
        }
        try {
            Long checkNumber = Long.valueOf(principalName);
            long count = assetService.countAssetsByCheckNumber(checkNumber);
            return (int) count;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private boolean hasRole(String roleName) {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> roleName.equals(authority.getAuthority()));
    }

    private boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    private boolean isManager() {
        return hasRole("ROLE_MANAGER");
    }

    private boolean isUser() {
        return hasRole("ROLE_USER");
    }

    private boolean hasNoRole() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream().count() == 0;
    }

    @GetMapping("/reports/user-assets")
    public String userAssetsReport(Model model,
                                   @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
                                   @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size,
                                   @org.springframework.web.bind.annotation.RequestParam(required = false) String searchTerm) {
        List<UserDto> users = userService.listAllUsers();
        
        // Filter by search term if provided
        if (searchTerm != null && !searchTerm.isBlank()) {
            String searchLower = searchTerm.toLowerCase().trim();
            users = users.stream()
                    .filter(user -> {
                        // Search by check number
                        if (user.getCheckNumber() != null && 
                            user.getCheckNumber().toString().contains(searchLower)) {
                            return true;
                        }
                        // Search by username
                        if (user.getUsername() != null && 
                            user.getUsername().toLowerCase().contains(searchLower)) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }
        
        List<ReportRow> allRows = users.stream()
                .map(user -> new ReportRow(
                        user.getId(),
                        computeFullName(user),
                        user.getCheckNumber(),
                        computeAssetCount(user)))
                .filter(row -> row.getAssetCount() > 0)
                .collect(Collectors.toList());

        int totalRows = allRows.size();
        int totalPages = totalRows == 0 ? 1 : (int) Math.ceil((double) totalRows / size);
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        int fromIndex = Math.min(page * size, totalRows);
        int toIndex = Math.min(fromIndex + size, totalRows);
        List<ReportRow> pageRows = allRows.subList(fromIndex, toIndex);

        model.addAttribute("reportRows", pageRows);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalRows", totalRows);
        model.addAttribute("searchTerm", searchTerm != null ? searchTerm : "");
        return "user-assets-report";
    }

    private int computeAssetCount(UserDto user) {
        if (user.getCheckNumber() != null) {
            long count = assetService.countUnreleasedAssetsByCheckNumber(user.getCheckNumber());
            if (count > 0) {
                return (int) count;
            }
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return (int) assetService.listAssetsForUser(user.getUsername()).stream()
                    .filter(asset -> !asset.isIsreleased())
                    .count();
        }
        return 0;
    }

    public static class ReportRow {
        private final Long id;
        private final String name;
        private final Long checkNumber;
        private final int assetCount;

        public ReportRow(Long id, String name, Long checkNumber, int assetCount) {
            this.id = id;
            this.name = name;
            this.checkNumber = checkNumber;
            this.assetCount = assetCount;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Long getCheckNumber() {
            return checkNumber;
        }

        public int getAssetCount() {
            return assetCount;
        }
    }

    private String computeFullName(UserDto user) {
        String firstName = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String lastName = user.getLastName() != null ? user.getLastName().trim() : "";
        String fullName = (firstName + " " + lastName).trim();
        if (fullName.isEmpty()) {
            fullName = user.getUsername();
        }
        return fullName;
    }
}
