package com.unisa.seproject.model.action;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Sealed interface representing what happens when a rule fires.
 *
 * <p>All leaf implementations are immutable records containing only configuration data.
 * {@link CompositeAction} is the sole non-record implementation and realises the
 * <b>Composite</b> pattern, allowing multiple actions to be executed as one.
 *
 * <p>Jackson polymorphism is handled via {@code @JsonTypeInfo} / {@code @JsonSubTypes}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageAction.class,       name = "MESSAGE"),
        @JsonSubTypes.Type(value = AudioAction.class,         name = "AUDIO"),
        @JsonSubTypes.Type(value = FileAction.class,          name = "FILE"),
        @JsonSubTypes.Type(value = FileAppendAction.class,    name = "FILE_APPEND"),
        @JsonSubTypes.Type(value = ExecuteProgramAction.class, name = "EXECUTE_PROGRAM"),
        @JsonSubTypes.Type(value = CompositeAction.class,     name = "COMPOSITE"),
})
public sealed interface Action
        permits MessageAction, AudioAction, FileAction,
                FileAppendAction, ExecuteProgramAction, CompositeAction {

    /** Executes this action. Implementations must be safe to call from a background thread. */
    void execute();
}
