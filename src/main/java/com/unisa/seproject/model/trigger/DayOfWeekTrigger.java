package com.unisa.seproject.model.trigger;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Fires on a specific day of the week (e.g. every Monday).
 *
 * <p>JSON example: {@code {"type":"DAY_OF_WEEK","dayOfWeek":"MONDAY"}}
 */
public record DayOfWeekTrigger(DayOfWeek dayOfWeek) implements Trigger {

    @Override
    public boolean isVerified() {
        return LocalDate.now().getDayOfWeek() == dayOfWeek;
    }
}
