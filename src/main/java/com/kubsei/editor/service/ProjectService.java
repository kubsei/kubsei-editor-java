package com.kubsei.editor.service;

import com.kubsei.editor.exception.AuthenticationException;
import com.kubsei.editor.model.Canvas;
import com.kubsei.editor.model.Layer;
import com.kubsei.editor.model.Project;
import com.kubsei.editor.model.Role;
import com.kubsei.editor.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAllByOrderByUpdatedAtDesc();
    }

    public List<Project> getProjectsByUser(String userId) {
        return projectRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    public List<Project> getAccessibleProjects(String userId) {
        return projectRepository.findAccessibleByUser(userId);
    }

    public Optional<Project> getProjectById(String id) {
        return projectRepository.findById(id);
    }

    public Optional<Project> getProjectByIdAndUser(String projectId, String userId, Set<Role> roles) {
        // Admin can see any project
        if (roles.contains(Role.ADMIN)) {
            return projectRepository.findById(projectId);
        }

        return projectRepository.findById(projectId)
                .filter(project ->
                        userId.equals(project.getUserId()) || project.isPublic());
    }

    public Project createProject(String name, Canvas canvasInput, String userId) {
        Canvas canvas = canvasInput != null ? canvasInput : Canvas.builder().build();

        // Ensure canvas has default values
        if (canvas.getWidth() == null) canvas.setWidth(1920);
        if (canvas.getHeight() == null) canvas.setHeight(1080);
        if (canvas.getZoom() == null) canvas.setZoom(1.0);
        if (canvas.getOffsetX() == null) canvas.setOffsetX(0.0);
        if (canvas.getOffsetY() == null) canvas.setOffsetY(0.0);

        // Create default layer
        Layer defaultLayer = Layer.builder()
                .id(UUID.randomUUID().toString())
                .name("Layer 1")
                .visible(true)
                .locked(false)
                .opacity(1.0)
                .elements(new ArrayList<>())
                .build();

        Project project = Project.builder()
                .name(name)
                .userId(userId)
                .canvas(canvas)
                .layers(new ArrayList<>(List.of(defaultLayer)))
                .isPublic(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Project saved = projectRepository.save(project);
        log.info("Created project: {} with id: {} for user: {}", name, saved.getId(), userId);
        return saved;
    }

    public Project updateProject(String id, String name, String userId, Set<Role> roles) {
        Project project = getProjectByIdAndUser(id, userId, roles)
                .orElseThrow(() -> new AuthenticationException("Project not found or access denied"));

        // Only owner or admin can edit
        if (!userId.equals(project.getUserId()) && !roles.contains(Role.ADMIN)) {
            throw new AuthenticationException("Only owner can edit this project");
        }

        if (name != null) {
            project.setName(name);
        }
        project.setUpdatedAt(Instant.now());

        Project saved = projectRepository.save(project);
        log.info("Updated project: {}", id);
        return saved;
    }

    public boolean deleteProject(String id, String userId, Set<Role> roles) {
        Project project = getProjectByIdAndUser(id, userId, roles)
                .orElseThrow(() -> new AuthenticationException("Project not found or access denied"));

        // Only owner or admin can delete
        if (!userId.equals(project.getUserId()) && !roles.contains(Role.ADMIN)) {
            throw new AuthenticationException("Only owner can delete this project");
        }

        projectRepository.delete(project);
        log.info("Deleted project: {}", id);
        return true;
    }

    @SuppressWarnings("unchecked")
    public Project saveProjectState(String id, Canvas canvas, Map<String, Object> elements,
                                     List<Layer> layers, String userId, Set<Role> roles) {
        Project project = getProjectByIdAndUser(id, userId, roles)
                .orElseThrow(() -> new AuthenticationException("Project not found or access denied"));

        // Only owner or admin can save state
        if (!userId.equals(project.getUserId()) && !roles.contains(Role.ADMIN)) {
            throw new AuthenticationException("Only owner can modify this project");
        }

        if (canvas != null) {
            Canvas existingCanvas = project.getCanvas();
            if (canvas.getWidth() != null) existingCanvas.setWidth(canvas.getWidth());
            if (canvas.getHeight() != null) existingCanvas.setHeight(canvas.getHeight());
            if (canvas.getZoom() != null) existingCanvas.setZoom(canvas.getZoom());
            if (canvas.getOffsetX() != null) existingCanvas.setOffsetX(canvas.getOffsetX());
            if (canvas.getOffsetY() != null) existingCanvas.setOffsetY(canvas.getOffsetY());
        }

        if (elements != null) {
            project.setElements(elements);
        }

        if (layers != null) {
            project.setLayers(layers);
        }

        project.setUpdatedAt(Instant.now());

        Project saved = projectRepository.save(project);
        log.info("Saved project state: {}", id);
        return saved;
    }

    public Project setProjectVisibility(String id, boolean isPublic, String userId, Set<Role> roles) {
        Project project = getProjectByIdAndUser(id, userId, roles)
                .orElseThrow(() -> new AuthenticationException("Project not found or access denied"));

        if (!userId.equals(project.getUserId()) && !roles.contains(Role.ADMIN)) {
            throw new AuthenticationException("Only owner can change project visibility");
        }

        project.setPublic(isPublic);
        project.setUpdatedAt(Instant.now());
        return projectRepository.save(project);
    }
}
