package com.unisa.seproject.service;

import com.unisa.seproject.model.rule.FiringMode;
import com.unisa.seproject.model.rule.Rule;
import com.unisa.seproject.repository.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service for CRUD operations on rules.
 *
 * <p>Enforces business invariants (default values, ID assignment) before delegating to
 * the {@link RuleRepository}. The controller layer must not bypass this service to
 * ensure invariants are always applied.
 */
@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    public Optional<Rule> getRuleById(String id) {
        return ruleRepository.findById(id);
    }

    /**
     * Creates a new rule. Assigns a UUID, applies defaults, and persists it.
     */
    public Rule createRule(Rule rule) {
        rule.setId(UUID.randomUUID().toString());
        rule.setActive(true);
        rule.setLastFiredAt(null);
        if (rule.getFiringMode() == null) {
            rule.setFiringMode(FiringMode.FIRE_MULTIPLE);
        }
        return ruleRepository.save(rule);
    }

    /**
     * Replaces an existing rule. Runtime state ({@code lastFiredAt}) is preserved
     * so the scheduler does not lose track of when the rule last fired.
     *
     * @return the updated rule, or {@link Optional#empty()} if the id is unknown
     */
    public Optional<Rule> updateRule(String id, Rule incoming) {
        return ruleRepository.findById(id).map(existing -> {
            incoming.setId(id);
            incoming.setLastFiredAt(existing.getLastFiredAt()); // preserve runtime state
            return ruleRepository.save(incoming);
        });
    }

    /**
     * Deletes a rule by id.
     *
     * @return {@code true} if the rule existed and was removed
     */
    public boolean deleteRule(String id) {
        if (ruleRepository.findById(id).isEmpty()) {
            return false;
        }
        ruleRepository.deleteById(id);
        return true;
    }

    /**
     * Toggles the {@code active} flag on a rule.
     *
     * @return the updated rule, or {@link Optional#empty()} if the id is unknown
     */
    public Optional<Rule> toggleActive(String id) {
        return ruleRepository.findById(id).map(rule -> {
            rule.setActive(!rule.isActive());
            return ruleRepository.save(rule);
        });
    }
}
