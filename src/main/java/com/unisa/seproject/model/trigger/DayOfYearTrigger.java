package com.unisa.seproject.model.trigger;

import java.time.LocalDate;

/**
 * Fires every year on the same month and day as {@code date} (year component is ignored).
 * This makes the trigger useful for recurring annual events such as birthdays or anniversaries.
 *
 * <p>JSON example: {@code {"type":"DAY_OF_YEAR","date":"2000-12-25"}}
 */
public record DayOfYearTrigger(LocalDate date) implements Trigger {

    @Override
    public boolean isVerified() {
        LocalDate today = LocalDate.now();
        return today.getMonthValue() == date.getMonthValue()
                && today.getDayOfMonth() == date.getDayOfMonth();
    }
}
