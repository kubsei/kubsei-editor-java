package com.kubsei.editor.resolver;

import com.kubsei.editor.model.Project;
import com.kubsei.editor.model.Role;
import com.kubsei.editor.service.ProjectService;
import graphql.GraphQLContext;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProjectQueryResolver {

    private final ProjectService projectService;

    @QueryMapping
    public List<Project> projects(GraphQLContext context) {
        requireAuthentication(context);
        String userId = context.get("userId");

        return projectService.getProjectsByUser(userId);
    }

    @QueryMapping
    public Project project(@Argument String id, GraphQLContext context) {
        requireAuthentication(context);
        String userId = context.get("userId");
        Set<Role> roles = extractRoles(context);

        return projectService.getProjectByIdAndUser(id, userId, roles)
                .orElseThrow(() -> new RuntimeException("Project not found or access denied"));
    }

    private void requireAuthentication(GraphQLContext context) {
        Boolean isAuthenticated = context.get("isAuthenticated");
        if (isAuthenticated == null || !isAuthenticated) {
            throw new RuntimeException("Authentication required");
        }
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
