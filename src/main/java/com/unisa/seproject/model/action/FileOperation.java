package com.unisa.seproject.model.action;

/**
 * The file-system operation performed by a {@link FileAction}.
 * Used as part of the Strategy pattern: the operation type selects the
 * execution branch inside {@link FileAction#execute()}.
 */
public enum FileOperation {
    COPY,
    MOVE,
    REMOVE
}
