package com.kubsei.editor.resolver.input;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProjectStateInput {
    private CanvasInput canvas;
    private Map<String, Object> elements;
    private List<LayerInput> layers;
}
