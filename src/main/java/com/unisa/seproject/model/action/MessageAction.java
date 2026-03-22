package com.unisa.seproject.model.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs a message when fired. In the old JavaFX project this showed an alert dialog;
 * in the backend the message is written to the application log so the React frontend
 * can later surface it (e.g. via a notifications endpoint or WebSocket).
 *
 * <p>JSON example: {@code {"type":"MESSAGE","message":"Backup completed"}}
 */
public record MessageAction(String message) implements Action {

    private static final Logger log = LoggerFactory.getLogger(MessageAction.class);

    @Override
    public void execute() {
        log.info("[MessageAction] {}", message);
    }
}
