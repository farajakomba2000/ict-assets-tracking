package com.example.ictassetstracking.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ictassetstracking.dto.AssetDto;
import com.example.ictassetstracking.dto.UserCreateRequest;
import com.example.ictassetstracking.dto.UserDto;
import com.example.ictassetstracking.dto.UserRolesRequest;
import com.example.ictassetstracking.dto.UserUpdateRequest;
import com.example.ictassetstracking.entity.Role;
import com.example.ictassetstracking.repository.EmployeeRepository;
import com.example.ictassetstracking.service.AssetService;
import com.example.ictassetstracking.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserViewController {

    private final UserService userService;
    private final AssetService assetService;
    private final EmployeeRepository employeeRepository;

    public UserViewController(UserService userService, AssetService assetService, EmployeeRepository employeeRepository) {
        this.userService = userService;
        this.assetService = assetService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/users")
    public String listUsers(Model model,
                            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
                            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size,
                            @org.springframework.web.bind.annotation.RequestParam(required = false) Long searchCheckNumber,
                            @org.springframework.web.bind.annotation.RequestParam(required = false) String searchName) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserDto> userPage;
        if (searchCheckNumber != null) {
            userPage = userService.findUsersByCheckNumber(searchCheckNumber, pageRequest);
        } else if (searchName != null && !searchName.isBlank()) {
            userPage = userService.findUsersByName(searchName, pageRequest);
        } else {
            userPage = userService.listUsers(pageRequest);
        }

        Map<Long, Long> assetCounts = new HashMap<>();
        for (UserDto user : userPage.getContent()) {
            long count = 0;
            if (user.getCheckNumber() != null) {
                count = assetService.countAssetsByCheckNumber(user.getCheckNumber());
            }
            if (count == 0 && user.getUsername() != null && !user.getUsername().isBlank()) {
                count = assetService.listAssetsForUser(user.getUsername()).size();
            }
            assetCounts.put(user.getId(), count);
        }

        List<Integer> pageNumbers = IntStream.range(0, userPage.getTotalPages())
                .boxed()
                .collect(Collectors.toList());

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("assetCounts", assetCounts);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("searchCheckNumber", searchCheckNumber);
        model.addAttribute("searchName", searchName);
        return "users";
    }

    @GetMapping({"/users/new", "/users/create"})
    public String createForm(Model model) {
        model.addAttribute("user", new UserCreateRequest());
        model.addAttribute("roles", Role.values());
        return "user-form";
    }

    @GetMapping("/users/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        UserDto dto = userService.getUserById(id);
        UserUpdateRequest update = new UserUpdateRequest();
        update.setEnabled(dto.isEnabled());
        update.setRoles(dto.getRoles());
        model.addAttribute("username", dto.getUsername());
        model.addAttribute("displayUsernameLabel", buildDisplayUsernameLabel(dto));
        model.addAttribute("update", update);
        model.addAttribute("roles", Role.values());
        model.addAttribute("id", id);
        return "user-form";
    }

    private String buildDisplayUsernameLabel(UserDto dto) {
        if (dto.getCheckNumber() != null) {
            return employeeRepository.findByCheckNumber(dto.getCheckNumber())
                    .map(employee -> {
                        String fullName = String.join(" ",
                                employee.getFirstName() != null ? employee.getFirstName().trim() : "",
                                employee.getLastName() != null ? employee.getLastName().trim() : ""
                        ).trim();
                        if (fullName.isBlank()) {
                            fullName = employee.getFullName();
                        }
                        if (fullName == null || fullName.isBlank()) {
                            fullName = dto.getUsername();
                        }
                        return String.format("%s (%d)", fullName, dto.getCheckNumber());
                    })
                    .orElse(String.format("%s (%d)", dto.getUsername(), dto.getCheckNumber()));
        }
        return dto.getUsername();
    }

    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute("user") UserCreateRequest request, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "user-form";
        }
        try {
            userService.createUser(request);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("roles", Role.values());
            return "user-form";
        }
        return "redirect:/users";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("update") UserUpdateRequest request, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("id", id);
            return "user-form";
        }
        try {
            userService.updateUser(id, request);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("roles", Role.values());
            model.addAttribute("id", id);
            return "user-form";
        }
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/roles/add")
    public String manageUserRolesForm(@PathVariable Long id, Model model) {
        UserDto dto = userService.getUserById(id);
        UserRolesRequest rolesRequest = new UserRolesRequest();
        rolesRequest.setRoles(dto.getRoles());

        model.addAttribute("user", dto);
        model.addAttribute("rolesRequest", rolesRequest);
        model.addAttribute("roles", Role.values());
        return "user-roles-form";
    }

    @PostMapping("/users/{id}/roles/add")
    public String updateUserRoles(@PathVariable Long id,
                                  @Valid @ModelAttribute("rolesRequest") UserRolesRequest request,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            UserDto dto = userService.getUserById(id);
            model.addAttribute("user", dto);
            model.addAttribute("roles", Role.values());
            return "user-roles-form";
        }

        userService.setRoles(id, request);
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/assets")
    public String viewUserAssets(@PathVariable Long id,
                                 @org.springframework.web.bind.annotation.RequestParam(required = false) Long checkNumber,
                                 Model model) {
        UserDto user = userService.getUserById(id);
        model.addAttribute("user", user);

        Long searchCheckNumber = checkNumber != null ? checkNumber : user.getCheckNumber();
        model.addAttribute("searchCheckNumber", searchCheckNumber);

        java.util.List<AssetDto> assets;
        if (searchCheckNumber != null) {
            assets = assetService.listUnreleasedAssetsByCheckNumber(searchCheckNumber);
        } else {
            assets = assetService.listAssetsForUser(user.getUsername()).stream()
                    .filter(a -> !a.isIsreleased())
                    .collect(Collectors.toList());
        }
        model.addAttribute("assets", assets);
        model.addAttribute("assetsCount", assets.size());
        return "user-assets";
    }

    @GetMapping("/users/{id}/reset-password")
    public String resetPasswordForm(@PathVariable Long id, Model model) {
        UserDto dto = userService.getUserById(id);
        UserUpdateRequest reset = new UserUpdateRequest();
        model.addAttribute("user", dto);
        model.addAttribute("reset", reset);
        model.addAttribute("id", id);
        return "user-reset-password";
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetPassword(@PathVariable Long id,
                                @Valid @ModelAttribute("reset") UserUpdateRequest request,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", userService.getUserById(id));
            model.addAttribute("id", id);
            return "user-reset-password";
        }
        try {
            userService.updateUser(id, request);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("user", userService.getUserById(id));
            model.addAttribute("id", id);
            return "user-reset-password";
        }
        return "redirect:/users";
    }
}
