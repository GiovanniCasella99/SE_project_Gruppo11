package com.unisa.seproject.model.trigger;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Sealed interface representing the condition that must be satisfied for a rule to fire.
 *
 * <p>Jackson polymorphism is handled via {@code @JsonTypeInfo} / {@code @JsonSubTypes} so that
 * triggers can be serialised to / deserialised from JSON without extra wrapper objects.
 *
 * <p>All permitted subtypes are immutable records, meaning their configuration is fixed at
 * creation time.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimeTrigger.class,            name = "TIME"),
        @JsonSubTypes.Type(value = DayOfWeekTrigger.class,       name = "DAY_OF_WEEK"),
        @JsonSubTypes.Type(value = DayOfMonthTrigger.class,      name = "DAY_OF_MONTH"),
        @JsonSubTypes.Type(value = DayOfYearTrigger.class,       name = "DAY_OF_YEAR"),
        @JsonSubTypes.Type(value = FileSizeTrigger.class,        name = "FILE_SIZE"),
        @JsonSubTypes.Type(value = FileInDirectoryTrigger.class, name = "FILE_IN_DIRECTORY"),
})
public sealed interface Trigger
        permits TimeTrigger, DayOfWeekTrigger, DayOfMonthTrigger,
                DayOfYearTrigger, FileSizeTrigger, FileInDirectoryTrigger {

    /** Returns {@code true} when the trigger condition is currently satisfied. */
    boolean isVerified();
}
