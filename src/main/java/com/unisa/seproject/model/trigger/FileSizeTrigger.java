package com.unisa.seproject.model.trigger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fires when the size of {@code filePath} is greater than or equal to {@code thresholdKb} kilobytes.
 * Returns {@code false} if the file does not exist or cannot be read.
 *
 * <p>JSON example: {@code {"type":"FILE_SIZE","filePath":"/tmp/log.txt","thresholdKb":1024}}
 */
public record FileSizeTrigger(String filePath, long thresholdKb) implements Trigger {

    @Override
    public boolean isVerified() {
        try {
            return Files.size(Path.of(filePath)) / 1024 >= thresholdKb;
        } catch (IOException e) {
            return false;
        }
    }
}
