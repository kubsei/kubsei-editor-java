package com.kubsei.editor.resolver.input;

import lombok.Data;

@Data
public class CreateProjectInput {
    private String name;
    private CanvasInput canvas;
}
