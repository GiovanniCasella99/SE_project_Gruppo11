package com.unisa.seproject.model.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Launches an external program. The process is started and the method returns immediately
 * (fire-and-forget); the process output is not captured.
 *
 * <p>JSON example:
 * <pre>{"type":"EXECUTE_PROGRAM","programPath":"/usr/bin/notify-send","args":["Hello"]}</pre>
 */
public record ExecuteProgramAction(String programPath, List<String> args) implements Action {

    private static final Logger log = LoggerFactory.getLogger(ExecuteProgramAction.class);

    @Override
    public void execute() {
        List<String> command = new ArrayList<>();
        command.add(programPath);
        if (args != null) {
            command.addAll(args);
        }
        try {
            new ProcessBuilder(command)
                    .directory(new java.io.File(programPath).getParentFile())
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            log.info("[ExecuteProgramAction] Launched '{}'", programPath);
        } catch (IOException e) {
            log.error("[ExecuteProgramAction] Failed to launch '{}': {}", programPath, e.getMessage());
        }
    }
}
