package com.unisa.seproject.model.trigger;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fires when a file named {@code fileName} exists inside {@code directoryPath}.
 * Returns {@code false} if the directory is not accessible.
 *
 * <p>JSON example: {@code {"type":"FILE_IN_DIRECTORY","directoryPath":"/tmp/inbox","fileName":"trigger.txt"}}
 */
public record FileInDirectoryTrigger(String directoryPath, String fileName) implements Trigger {

    @Override
    public boolean isVerified() {
        return Files.exists(Path.of(directoryPath, fileName));
    }
}
