package com.unisa.seproject.service;

import com.unisa.seproject.engine.strategy.FiringStrategy;
import com.unisa.seproject.engine.strategy.FiringStrategyFactory;
import com.unisa.seproject.event.RuleFiredEvent;
import com.unisa.seproject.model.action.MessageAction;
import com.unisa.seproject.model.rule.Rule;
import com.unisa.seproject.repository.RuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Core evaluation engine: iterates over all rules and fires those whose conditions are met.
 *
 * <p>Called periodically by {@link com.unisa.seproject.scheduler.RuleScheduler}.
 * Each evaluation cycle:
 * <ol>
 *   <li>Skips inactive or incomplete rules.</li>
 *   <li>Calls {@link com.unisa.seproject.model.trigger.Trigger#isVerified()} on the trigger.</li>
 *   <li>Delegates to the appropriate {@link FiringStrategy} (resolved via
 *       {@link FiringStrategyFactory}) to decide if the rule may fire again.</li>
 *   <li>Executes the action and calls {@link FiringStrategy#onFired} to update
 *       {@code lastFiredAt} on the rule.</li>
 *   <li>Saves the updated rule so {@code lastFiredAt} survives a restart.</li>
 * </ol>
 */
@Service
public class RuleEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(RuleEvaluationService.class);

    private final RuleRepository ruleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RuleEvaluationService(RuleRepository ruleRepository, ApplicationEventPublisher eventPublisher) {
        this.ruleRepository = ruleRepository;
        this.eventPublisher = eventPublisher;
    }

    /** Entry point called by the scheduler every tick. */
    public void evaluateAll() {
        ruleRepository.findAll().forEach(this::evaluate);
    }

    private void evaluate(Rule rule) {
        if (!rule.isActive()) return;
        if (rule.getTrigger() == null || rule.getAction() == null) return;

        boolean triggered = rule.getTrigger().isVerified();
        if (!triggered) return;

        FiringStrategy strategy = FiringStrategyFactory.forMode(rule.getFiringMode());
        if (!strategy.canFire(rule)) return;

        log.info("Rule '{}' fired.", rule.getName());
        try {
            rule.getAction().execute();
        } catch (Exception e) {
            log.error("Action execution failed for rule '{}': {}", rule.getName(), e.getMessage());
        }

        strategy.onFired(rule);
        ruleRepository.save(rule); // persist lastFiredAt update

        String firedAt = rule.getLastFiredAt() != null
                ? rule.getLastFiredAt().toString()
                : java.time.LocalDateTime.now().toString();
        String message = rule.getAction() instanceof MessageAction m ? m.message() : null;
        eventPublisher.publishEvent(new RuleFiredEvent(this, rule.getId(), rule.getName(), firedAt, message));
    }
}
