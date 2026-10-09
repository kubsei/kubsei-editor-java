package com.kubsei.editor.resolver.input;

import lombok.Data;

@Data
public class CanvasInput {
    private Integer width;
    private Integer height;
    private Double zoom;
    private Double offsetX;
    private Double offsetY;
}
