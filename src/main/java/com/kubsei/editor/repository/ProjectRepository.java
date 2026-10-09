package com.kubsei.editor.repository;

import com.kubsei.editor.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findAllByOrderByUpdatedAtDesc();

    List<Project> findByUserIdOrderByUpdatedAtDesc(String userId);

    Optional<Project> findByIdAndUserId(String id, String userId);

    List<Project> findByIsPublicTrue();

    @Query("{ '$or': [ { 'userId': ?0 }, { 'isPublic': true } ] }")
    List<Project> findAccessibleByUser(String userId);

    boolean existsByIdAndUserId(String id, String userId);

    long countByUserId(String userId);

    void deleteAllByUserId(String userId);
}
