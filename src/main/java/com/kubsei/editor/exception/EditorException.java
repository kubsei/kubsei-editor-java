package com.kubsei.editor.exception;

import org.springframework.graphql.execution.ErrorType;

/** An error with the GraphQL classification the client receives (extensions.classification) and translates. */
public class EditorException extends RuntimeException {

    private final ErrorType type;

    public EditorException(ErrorType type, String message) {
        super(message);
        this.type = type;
    }

    public ErrorType getType() {
        return type;
    }
}
