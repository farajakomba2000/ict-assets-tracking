package com.example.ictassetstracking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ictassetstracking.entity.UserAccount;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByCheckNumber(Long checkNumber);
    Page<UserAccount> findByCheckNumber(Long checkNumber, Pageable pageable);
    boolean existsByUsername(String username);
    boolean existsByCheckNumber(Long checkNumber);

    @Query("SELECT DISTINCT u.departmentName FROM UserAccount u WHERE u.departmentName IS NOT NULL AND u.departmentName <> '' ORDER BY u.departmentName")
    List<String> findDistinctDepartmentNames();
    
    @Query("SELECT u FROM UserAccount u WHERE " +
           "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<UserAccount> searchByName(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT u FROM UserAccount u WHERE " +
           "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<UserAccount> findByName(@Param("searchTerm") String searchTerm, Pageable pageable);
}
