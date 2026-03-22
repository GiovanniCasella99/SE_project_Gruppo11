package com.unisa.seproject.model.rule;

/**
 * Determines how a Rule behaves after its trigger is first satisfied.
 *
 * <ul>
 *   <li>FIRE_MULTIPLE – fires every time the trigger is verified (default)</li>
 *   <li>FIRE_ONCE     – fires only the first time; never again after that</li>
 *   <li>SLEEP         – fires, then waits {@code sleepMinutes} before being
 *                       eligible to fire again</li>
 * </ul>
 *
 * Consumed by the Strategy pattern: each value maps to a {@link com.unisa.seproject.engine.strategy.FiringStrategy}
 * implementation selected at evaluation time.
 */
public enum FiringMode {
    FIRE_MULTIPLE,
    FIRE_ONCE,
    SLEEP
}
