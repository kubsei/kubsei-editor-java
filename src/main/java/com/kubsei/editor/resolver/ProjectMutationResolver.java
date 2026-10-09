package com.kubsei.editor.resolver;

import com.kubsei.editor.model.Canvas;
import com.kubsei.editor.model.Layer;
import com.kubsei.editor.model.Project;
import com.kubsei.editor.model.Role;
import com.kubsei.editor.resolver.input.CreateProjectInput;
import com.kubsei.editor.resolver.input.ProjectStateInput;
import com.kubsei.editor.resolver.input.UpdateProjectInput;
import com.kubsei.editor.service.ProjectService;
import graphql.GraphQLContext;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProjectMutationResolver {

    private final ProjectService projectService;

    @MutationMapping
    public Project createProject(@Argument CreateProjectInput input, GraphQLContext context) {
        requireAuthentication(context);
        requireRole(context, Role.EDITOR, Role.ADMIN);

        String userId = context.get("userId");

        Canvas canvas = null;
        if (input.getCanvas() != null) {
            canvas = Canvas.builder()
                    .width(input.getCanvas().getWidth())
                    .height(input.getCanvas().getHeight())
                    .zoom(input.getCanvas().getZoom())
                    .offsetX(input.getCanvas().getOffsetX())
                    .offsetY(input.getCanvas().getOffsetY())
                    .build();
        }
        return projectService.createProject(input.getName(), canvas, userId);
    }

    @MutationMapping
    public Project updateProject(@Argument String id, @Argument UpdateProjectInput input,
                                  GraphQLContext context) {
        requireAuthentication(context);
        requireRole(context, Role.EDITOR, Role.ADMIN);

        String userId = context.get("userId");
        Set<Role> roles = extractRoles(context);

        return projectService.updateProject(id, input.getName(), userId, roles);
    }

    @MutationMapping
    public Boolean deleteProject(@Argument String id, GraphQLContext context) {
        requireAuthentication(context);
        requireRole(context, Role.EDITOR, Role.ADMIN);

        String userId = context.get("userId");
        Set<Role> roles = extractRoles(context);

        return projectService.deleteProject(id, userId, roles);
    }

    @MutationMapping
    @SuppressWarnings("unchecked")
    public Project saveProjectState(@Argument String id, @Argument ProjectStateInput state,
                                     GraphQLContext context) {
        requireAuthentication(context);
        requireRole(context, Role.EDITOR, Role.ADMIN);

        String userId = context.get("userId");
        Set<Role> roles = extractRoles(context);

        Canvas canvas = null;
        if (state.getCanvas() != null) {
            canvas = Canvas.builder()
                    .width(state.getCanvas().getWidth())
                    .height(state.getCanvas().getHeight())
                    .zoom(state.getCanvas().getZoom())
                    .offsetX(state.getCanvas().getOffsetX())
                    .offsetY(state.getCanvas().getOffsetY())
                    .build();
        }

        Map<String, Object> elements = state.getElements();

        List<Layer> layers = null;
        if (state.getLayers() != null) {
            layers = state.getLayers().stream()
                    .map(l -> Layer.builder()
                            .id(l.getId())
                            .name(l.getName())
                            .visible(l.getVisible())
                            .locked(l.getLocked())
                            .opacity(l.getOpacity())
                            .elements(l.getElements())
                            .build())
                    .collect(Collectors.toList());
        }

        return projectService.saveProjectState(id, canvas, elements, layers, userId, roles);
    }

    @MutationMapping
    public Project setProjectVisibility(@Argument String id, @Argument Boolean isPublic,
                                         GraphQLContext context) {
        requireAuthentication(context);

        String userId = context.get("userId");
        Set<Role> roles = extractRoles(context);

        return projectService.setProjectVisibility(id, isPublic, userId, roles);
    }

    private void requireAuthentication(GraphQLContext context) {
        Boolean isAuthenticated = context.get("isAuthenticated");
        if (isAuthenticated == null || !isAuthenticated) {
            throw new RuntimeException("Authentication required");
        }
    }

    private void requireRole(GraphQLContext context, Role... requiredRoles) {
        Set<Role> userRoles = extractRoles(context);

        for (Role required : requiredRoles) {
            if (userRoles.contains(required)) {
                return;
            }
        }

        throw new RuntimeException("Insufficient permissions. Required roles: " +
                java.util.Arrays.toString(requiredRoles));
    }

    @SuppressWarnings("unchecked")
    private Set<Role> extractRoles(GraphQLContext context) {
        Collection<GrantedAuthority> authorities = context.get("userRoles");
        if (authorities == null) return Set.of();

        return authorities.stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
