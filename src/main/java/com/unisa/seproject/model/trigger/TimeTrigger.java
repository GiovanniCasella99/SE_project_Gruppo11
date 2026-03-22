package com.unisa.seproject.model.trigger;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Fires when the current wall-clock time (hour + minute) matches {@code time}.
 * Seconds are ignored so the trigger stays true for the entire matching minute.
 *
 * <p>JSON example: {@code {"type":"TIME","time":"10:30"}}
 */
public record TimeTrigger(LocalTime time) implements Trigger {

    @Override
    public boolean isVerified() {
        return LocalTime.now().truncatedTo(ChronoUnit.MINUTES).equals(time);
    }
}
