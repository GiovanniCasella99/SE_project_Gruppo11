package com.unisa.seproject.engine.strategy;

import com.unisa.seproject.model.rule.Rule;

import java.time.LocalDateTime;

/**
 * Fires every time the trigger is verified, with no restriction on how many times it has
 * fired before. This is the default behaviour (equivalent to the old {@code FireMultipleVerified}).
 */
public class FireMultipleStrategy implements FiringStrategy {

    @Override
    public boolean canFire(Rule rule) {
        return true;
    }

    @Override
    public void onFired(Rule rule) {
        rule.setLastFiredAt(LocalDateTime.now());
    }
}
