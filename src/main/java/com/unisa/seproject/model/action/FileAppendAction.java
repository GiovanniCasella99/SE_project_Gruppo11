package com.unisa.seproject.model.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Appends {@code content} to a text file. The file is created if it does not exist.
 *
 * <p>JSON example:
 * <pre>{"type":"FILE_APPEND","filePath":"/tmp/log.txt","content":"Rule fired at runtime\n"}</pre>
 */
public record FileAppendAction(String filePath, String content) implements Action {

    private static final Logger log = LoggerFactory.getLogger(FileAppendAction.class);

    @Override
    public void execute() {
        Path path = Path.of(filePath);
        try {
            Files.writeString(path, content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            log.info("[FileAppendAction] Appended to '{}'", filePath);
        } catch (IOException e) {
            log.error("[FileAppendAction] Failed to write to '{}': {}", filePath, e.getMessage());
        }
    }
}
