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

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT e.workstationName FROM Employee e WHERE e.workstationName IS NOT NULL AND TRIM(e.workstationName) <> '' ORDER BY e.workstationName")
    java.util.List<String> findDistinctWorkstationNames();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT e.departmentName FROM Employee e WHERE e.departmentName IS NOT NULL AND TRIM(e.departmentName) <> '' ORDER BY e.departmentName")
    java.util.List<String> findDistinctDepartmentNames();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT e.sectionName FROM Employee e WHERE e.sectionName IS NOT NULL AND TRIM(e.sectionName) <> '' ORDER BY e.sectionName")
    java.util.List<String> findDistinctSectionNames();
}
