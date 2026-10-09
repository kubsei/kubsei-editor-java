package com.kubsei.editor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Layer {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Builder.Default
    private String name = "Layer 1";

    @Builder.Default
    private Boolean visible = true;

    @Builder.Default
    private Boolean locked = false;

    @Builder.Default
    private Double opacity = 1.0;

    @Builder.Default
    private List<String> elements = new ArrayList<>();
}
