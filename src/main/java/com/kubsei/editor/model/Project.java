package com.kubsei.editor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String name;

    @Builder.Default
    private boolean isPublic = false;

    @Builder.Default
    private Canvas canvas = new Canvas();

    @Builder.Default
    private Map<String, Object> elements = new HashMap<>();

    @Builder.Default
    private List<Layer> layers = new ArrayList<>();

    private String thumbnail;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
