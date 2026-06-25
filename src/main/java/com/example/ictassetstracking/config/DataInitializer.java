package com.example.ictassetstracking.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.ictassetstracking.entity.Category;
import com.example.ictassetstracking.entity.Employee;
import com.example.ictassetstracking.entity.MainCategory;
import com.example.ictassetstracking.entity.Role;
import com.example.ictassetstracking.entity.UserAccount;
import com.example.ictassetstracking.repository.AssetRepository;
import com.example.ictassetstracking.repository.CategoryRepository;
import com.example.ictassetstracking.repository.EmployeeRepository;
import com.example.ictassetstracking.repository.MainCategoryRepository;
import com.example.ictassetstracking.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           MainCategoryRepository mainCategoryRepository,
                           CategoryRepository categoryRepository,
                           AssetRepository assetRepository,
                           EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mainCategoryRepository = mainCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.assetRepository = assetRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Only initialize data if it doesn't already exist in the database
        if (mainCategoryRepository.count() == 0) {
            createDefaultMainCategory("Computing Devices", Set.of("Laptop", "Desktop Computer", "Server"));
            createDefaultMainCategory("Networking Equipment", Set.of("Switch", "Router", "Firewall", "Wi-Fi access point", "Modem", "Virtual Machine (VM)", "Virtual Private Network (VPN)"));
            createDefaultMainCategory("Mobile Device", Set.of("Smartphone", "Tablet", "iPad"));
            createDefaultMainCategory("Peripherals", Set.of(
                    "Scanner",
                    "Barcode / QR reader",
                    "Biometric scanner",
                    "Microphone",
                    "Webcam",
                    "Digital camera",
                    "Camcorder",
                    "Joystick",
                    "Light pen",
                    "MIDI keyboard",
                    "Electronic musical instrument",
                    "Plotter",
                    "Speaker",
                    "Headphone",
                    "Headset"
            ));
            createDefaultMainCategory("Software", Set.of("Windows", "macOS", "Linux", "iOS", "Antivirus", "Office application", "Graphic Design Software"));
            createDefaultMainCategory("Media & Audiovisual (AV) Equipment", Set.of("Projector", "Audio Recorder", "Recording Tape", "Drone", "Microphone", "TV display"));
        }
        
        // Initialize default users if they don't exist
        createDefaultUser("admin", "admin123", Set.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER, Role.ROLE_USER), 1001L, "Admin", "User");
        createDefaultUser("manager", "manager123", Set.of(Role.ROLE_MANAGER, Role.ROLE_USER), 1002L, "Manager", "User");
        createDefaultUser("user", "user123", Set.of(Role.ROLE_USER), 1003L, "Regular", "User");
    }

    private void createDefaultMainCategory(String mainName, Set<String> categoryNames) {
        MainCategory mainCategory = mainCategoryRepository.findByName(mainName)
                .orElseGet(() -> mainCategoryRepository.save(new MainCategory(mainName)));

        for (String categoryName : categoryNames) {
            Category category = new Category(categoryName, mainCategory);
            categoryRepository.save(category);
        }
    }

    private void createDefaultUser(String username, String password, Set<Role> roles, Long checkNumber, String firstName, String lastName) {
        if (userRepository.existsByUsername(username) || (checkNumber != null && userRepository.existsByCheckNumber(checkNumber))) {
            return;
        }

        // Create Employee record for this user
        if (!employeeRepository.existsByCheckNumber(checkNumber)) {
            Employee employee = new Employee();
            employee.setCheckNumber(checkNumber);
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setFullName(firstName + " " + lastName);
            employeeRepository.save(employee);
        }

        // Create UserAccount
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setCheckNumber(checkNumber);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setRoles(roles);
        user.setFirstName(firstName);
        userRepository.save(user);
    }
}
