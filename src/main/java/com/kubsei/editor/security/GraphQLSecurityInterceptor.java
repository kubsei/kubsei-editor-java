package com.kubsei.editor.security;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GraphQLSecurityInterceptor implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            request.configureExecutionInput((executionInput, builder) ->
                builder.graphQLContext(contextBuilder -> {
                    contextBuilder.put("userId", jwtAuth.getName());
                    contextBuilder.put("userEmail", jwtAuth.getToken().getClaimAsString("email"));
                    // Only roles: Spring Security 7 also adds factor authorities (FACTOR_BEARER)
                    contextBuilder.put("userRoles", jwtAuth.getAuthorities().stream()
                            .filter(authority -> authority.getAuthority().startsWith("ROLE_")).toList());
                    contextBuilder.put("isAuthenticated", true);
                }).build()
            );
        } else {
            request.configureExecutionInput((executionInput, builder) ->
                builder.graphQLContext(contextBuilder -> {
                    contextBuilder.put("isAuthenticated", false);
                }).build()
            );
        }

        return chain.next(request);
    }
}
