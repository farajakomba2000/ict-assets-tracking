package com.example.ictassetstracking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ictassetstracking.entity.Category;
import com.example.ictassetstracking.entity.MainCategory;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndMainCategory(String name, MainCategory mainCategory);
}
