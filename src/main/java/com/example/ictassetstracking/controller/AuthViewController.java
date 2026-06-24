package com.example.ictassetstracking.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ictassetstracking.dto.SignupRequest;
import com.example.ictassetstracking.entity.Employee;
import com.example.ictassetstracking.entity.Role;
import com.example.ictassetstracking.entity.UserAccount;
import com.example.ictassetstracking.repository.EmployeeRepository;
import com.example.ictassetstracking.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
public class AuthViewController {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthViewController(UserRepository userRepository, EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupRequest signupRequest,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return "signup";
        }
        java.util.Optional<Employee> employeeOpt = employeeRepository.findByCheckNumber(signupRequest.getCheckNumber());
        if (employeeOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Employee not found for check number: " + signupRequest.getCheckNumber());
            return "signup";
        }
        Employee employee = employeeOpt.get();
        model.addAttribute("employeeName", employee.getUsername());
        if (userRepository.existsByCheckNumber(signupRequest.getCheckNumber())) {
            model.addAttribute("errorMessage", "Check number already registered");
            return "signup";
        }
        UserAccount user = new UserAccount();
        user.setUsername(employee.getUsername());
        user.setCheckNumber(signupRequest.getCheckNumber());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEnabled(true);
        user.setRoles(java.util.Collections.singleton(Role.ROLE_USER));
        userRepository.save(user);
        return "redirect:/login";
    }
}
