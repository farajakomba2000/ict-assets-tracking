package com.example.ictassetstracking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ictassetstracking.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByCheckNumber(Long checkNumber);
    boolean existsByCheckNumber(Long checkNumber);

    java.util.Optional<Employee> findByFullName(String fullName);
    
    @org.springframework.data.jpa.repository.Query("SELECT e FROM Employee e WHERE LOWER(e.fullName) = LOWER(?1)")
    java.util.Optional<Employee> findByFullNameIgnoreCase(String fullName);
}
