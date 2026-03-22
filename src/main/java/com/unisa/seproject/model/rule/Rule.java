package com.unisa.seproject.model.rule;

import com.unisa.seproject.model.action.Action;
import com.unisa.seproject.model.trigger.Trigger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Central domain entity: a named rule that binds a {@link Trigger} to an {@link Action}.
 *
 * <p>Mutable fields ({@code active}, {@code lastFiredAt}) represent runtime state that is
 * persisted to JSON so the engine can resume correctly after a restart.
 *
 * <p>The {@code firingMode} / {@code sleepMinutes} pair drives the Strategy pattern:
 * see {@link com.unisa.seproject.engine.strategy.FiringStrategy}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

    /** UUID assigned on creation; immutable after that. */
    private String id;

    private String name;

    /** Condition that must be true for the rule to fire. */
    private Trigger trigger;

    /** Action executed when the rule fires. */
    private Action action;

    /** Whether the rule is enabled. Disabled rules are skipped by the scheduler. */
    private boolean active = true;

    /** Controls re-firing behaviour; defaults to {@link FiringMode#FIRE_MULTIPLE}. */
    private FiringMode firingMode = FiringMode.FIRE_MULTIPLE;

    /**
     * Cooldown period in minutes. Only meaningful when {@code firingMode == SLEEP}.
     * The rule will not fire again until {@code lastFiredAt + sleepMinutes} has passed.
     */
    private long sleepMinutes = 0;

    /**
     * Timestamp of the last successful firing. {@code null} means the rule has never fired.
     * Updated by the {@link com.unisa.seproject.engine.strategy.FiringStrategy} after each firing.
     */
    private LocalDateTime lastFiredAt;
}
