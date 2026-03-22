package com.unisa.seproject.engine.strategy;

import com.unisa.seproject.model.rule.Rule;

/**
 * <b>Strategy pattern</b> — defines the contract for deciding when a rule is
 * allowed to fire again after having already fired at least once.
 *
 * <p>The three concrete implementations correspond to the three {@link com.unisa.seproject.model.rule.FiringMode}
 * values and replace the old {@code VerifiedTool} / {@code FireOnceVerified} /
 * {@code FireMultipleVerified} / {@code SleepVerified} hierarchy.
 *
 * <p>Key improvements over the old design:
 * <ul>
 *   <li>Firing state is stored on {@link Rule#getLastFiredAt()} (the entity), not inside
 *       the {@code Action} — separating <em>data</em> from <em>behaviour</em>.</li>
 *   <li>The strategy is stateless; a single shared instance per mode is sufficient.</li>
 *   <li>Selection of the concrete strategy is done by
 *       {@link com.unisa.seproject.engine.strategy.FiringStrategyFactory}, following the
 *       Factory pattern.</li>
 * </ul>
 */
public interface FiringStrategy {

    /**
     * Returns {@code true} when the rule is allowed to fire right now, taking its
     * execution history into account.
     *
     * @param rule the rule being evaluated (never {@code null})
     */
    boolean canFire(Rule rule);

    /**
     * Called by the evaluation service immediately after the rule fires.
     * Implementations update {@link Rule#setLastFiredAt} (and any other state) here.
     *
     * @param rule the rule that just fired
     */
    void onFired(Rule rule);
}
