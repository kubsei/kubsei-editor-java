package com.kubsei.editor.resolver.input;

import lombok.Data;

import java.util.List;

@Data
public class LayerInput {
    private String id;
    private String name;
    private Boolean visible;
    private Boolean locked;
    private Double opacity;
    private List<String> elements;
}
