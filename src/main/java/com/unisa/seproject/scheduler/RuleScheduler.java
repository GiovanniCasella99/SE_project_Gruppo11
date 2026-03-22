package com.unisa.seproject.scheduler;

import com.unisa.seproject.service.RuleEvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodic trigger for the rule evaluation engine.
 *
 * <p>Replaces the old {@code CheckRuleThread} (a raw daemon thread with {@code Thread.sleep}).
 * Using {@code @Scheduled} hands lifecycle management to Spring's task scheduler, which
 * is backed by a managed thread pool — safer and more observable than bare threads.
 *
 * <p>The interval is configurable via {@code rule.check.interval} (milliseconds, default 10 s).
 */
@Component
public class RuleScheduler {

    private static final Logger log = LoggerFactory.getLogger(RuleScheduler.class);

    private final RuleEvaluationService evaluationService;

    public RuleScheduler(RuleEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @Scheduled(fixedRateString = "${rule.check.interval:10000}")
    public void checkRules() {
        log.debug("Evaluating rules...");
        evaluationService.evaluateAll();
    }
}
