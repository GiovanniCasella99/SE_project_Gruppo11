package com.unisa.seproject.engine.strategy;

import com.unisa.seproject.model.rule.Rule;

import java.time.LocalDateTime;

/**
 * Fires exactly once: only when the rule has never fired before ({@code lastFiredAt == null}).
 * After the first firing the rule is effectively dormant even if its trigger stays true.
 * Equivalent to the old {@code FireOnceVerified}.
 */
public class FireOnceStrategy implements FiringStrategy {

    @Override
    public boolean canFire(Rule rule) {
        return rule.getLastFiredAt() == null;
    }

    @Override
    public void onFired(Rule rule) {
        rule.setLastFiredAt(LocalDateTime.now());
    }
}
