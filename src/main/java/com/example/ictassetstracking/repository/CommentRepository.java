package com.example.ictassetstracking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ictassetstracking.entity.Asset;
import com.example.ictassetstracking.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAssetOrderByCreatedAtDesc(Asset asset);
}
