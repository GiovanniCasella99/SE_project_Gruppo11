package com.unisa.seproject.model.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Performs a file-system operation (copy, move, or remove) on {@code sourcePath}.
 * The specific operation is selected via the {@link FileOperation} enum,
 * which acts as the <em>concrete strategy selector</em> within this class.
 *
 * <p>Uses {@link java.nio.file.Path} throughout for cross-platform compatibility.
 *
 * <p>JSON example:
 * <pre>{"type":"FILE","sourcePath":"/tmp/a.txt","destinationPath":"/backup/","operation":"COPY"}</pre>
 */
public record FileAction(String sourcePath, String destinationPath, FileOperation operation) implements Action {

    private static final Logger log = LoggerFactory.getLogger(FileAction.class);

    @Override
    public void execute() {
        Path source = Path.of(sourcePath);
        switch (operation) {
            case COPY -> {
                Path dest = resolveDestination(source);
                try {
                    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                    log.info("[FileAction] Copied '{}' → '{}'", source, dest);
                } catch (IOException e) {
                    log.error("[FileAction] Copy failed: {}", e.getMessage());
                }
            }
            case MOVE -> {
                Path dest = resolveDestination(source);
                try {
                    Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
                    log.info("[FileAction] Moved '{}' → '{}'", source, dest);
                } catch (IOException e) {
                    log.error("[FileAction] Move failed: {}", e.getMessage());
                }
            }
            case REMOVE -> {
                try {
                    Files.delete(source);
                    log.info("[FileAction] Deleted '{}'", source);
                } catch (IOException e) {
                    log.error("[FileAction] Delete failed: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * If {@code destinationPath} points to an existing directory, appends the
     * source filename so we never overwrite the directory itself.
     */
    private Path resolveDestination(Path source) {
        Path dest = Path.of(destinationPath);
        if (Files.isDirectory(dest)) {
            return dest.resolve(source.getFileName());
        }
        return dest;
    }
}
