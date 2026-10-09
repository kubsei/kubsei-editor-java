package com.kubsei.editor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Canvas {

    @Builder.Default
    private Integer width = 1920;

    @Builder.Default
    private Integer height = 1080;

    @Builder.Default
    private Double zoom = 1.0;

    @Builder.Default
    private Double offsetX = 0.0;

    @Builder.Default
    private Double offsetY = 0.0;
}
