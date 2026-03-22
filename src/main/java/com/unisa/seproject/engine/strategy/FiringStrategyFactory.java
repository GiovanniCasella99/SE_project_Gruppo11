package com.unisa.seproject.engine.strategy;

import com.unisa.seproject.model.rule.FiringMode;

import java.util.Map;

/**
 * <b>Factory pattern</b> — maps each {@link FiringMode} enum value to its corresponding
 * {@link FiringStrategy} implementation.
 *
 * <p>Strategies are stateless, so a single shared instance per mode is used (effectively
 * a Flyweight). Adding a new firing mode only requires adding an entry here and a new
 * strategy class; no other code needs to change.
 */
public final class FiringStrategyFactory {

    private static final Map<FiringMode, FiringStrategy> STRATEGIES = Map.of(
            FiringMode.FIRE_MULTIPLE, new FireMultipleStrategy(),
            FiringMode.FIRE_ONCE,     new FireOnceStrategy(),
            FiringMode.SLEEP,         new SleepStrategy()
    );

    private FiringStrategyFactory() {}

    /**
     * Returns the strategy for the given mode.
     *
     * @throws IllegalArgumentException if the mode has no registered strategy
     */
    public static FiringStrategy forMode(FiringMode mode) {
        FiringStrategy strategy = STRATEGIES.get(mode);
        if (strategy == null) {
            throw new IllegalArgumentException("No FiringStrategy registered for mode: " + mode);
        }
        return strategy;
    }
}
