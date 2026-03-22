package com.unisa.seproject.model.trigger;

import java.time.LocalDate;

/**
 * Fires on a specific day of the month (1–31).
 *
 * <p>JSON example: {@code {"type":"DAY_OF_MONTH","day":15}}
 */
public record DayOfMonthTrigger(int day) implements Trigger {

    @Override
    public boolean isVerified() {
        return LocalDate.now().getDayOfMonth() == day;
    }
}
