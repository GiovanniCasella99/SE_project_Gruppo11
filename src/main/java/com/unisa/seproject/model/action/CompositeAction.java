package com.unisa.seproject.model.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * <b>Composite pattern</b> implementation for {@link Action}.
 *
 * <p>Groups an ordered list of actions and executes them sequentially as a single unit.
 * This was planned (via the {@code add/remove/getChild} methods) in the original project
 * but never implemented. Here it is a first-class citizen.
 *
 * <p>JSON example:
 * <pre>
 * {
 *   "type": "COMPOSITE",
 *   "actions": [
 *     {"type": "MESSAGE", "message": "Starting backup"},
 *     {"type": "FILE",    "sourcePath": "/data", "destinationPath": "/backup", "operation": "COPY"},
 *     {"type": "MESSAGE", "message": "Backup done"}
 *   ]
 * }
 * </pre>
 */
public final class CompositeAction implements Action {

    private final List<Action> actions;

    @JsonCreator
    public CompositeAction(@JsonProperty("actions") List<Action> actions) {
        this.actions = List.copyOf(actions);
    }

    public List<Action> getActions() {
        return actions;
    }

    /** Executes each contained action in order. Failures in one action do not stop the rest. */
    @Override
    public void execute() {
        actions.forEach(Action::execute);
    }

    @Override
    public String toString() {
        return "CompositeAction{actions=" + actions + "}";
    }
}
