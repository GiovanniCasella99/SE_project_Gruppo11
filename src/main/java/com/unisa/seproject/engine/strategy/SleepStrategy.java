package com.unisa.seproject.engine.strategy;

import com.unisa.seproject.model.rule.Rule;

import java.time.LocalDateTime;

/**
 * Fires when the trigger is verified AND the cooldown period has elapsed since the last firing.
 * The cooldown length is taken from {@link Rule#getSleepMinutes()}.
 *
 * <p>Equivalent to the old {@code SleepVerified}, but without the side-effect of mutating
 * {@code wakeUp} inside {@code verified()} — state is only updated in {@link #onFired}.
 */
public class SleepStrategy implements FiringStrategy {

    @Override
    public boolean canFire(Rule rule) {
        if (rule.getLastFiredAt() == null) {
            return true;
        }
        LocalDateTime wakeUp = rule.getLastFiredAt().plusMinutes(rule.getSleepMinutes());
        return !LocalDateTime.now().isBefore(wakeUp);
    }

    @Override
    public void onFired(Rule rule) {
        rule.setLastFiredAt(LocalDateTime.now());
    }
}
