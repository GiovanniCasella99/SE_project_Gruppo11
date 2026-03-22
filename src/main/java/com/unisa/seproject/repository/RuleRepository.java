package com.unisa.seproject.repository;

import com.unisa.seproject.model.rule.Rule;

import java.util.List;
import java.util.Optional;

/**
 * <b>Repository pattern</b> — abstracts all data-access operations for {@link Rule}.
 *
 * <p>Decouples the service layer from the persistence mechanism. Swapping from JSON
 * to a database (or any other store) only requires a new implementation of this interface;
 * the service layer stays unchanged.
 */
public interface RuleRepository {

    /** Returns an unmodifiable snapshot of all rules. */
    List<Rule> findAll();

    /** Finds a rule by its UUID, or {@link Optional#empty()} if not found. */
    Optional<Rule> findById(String id);

    /**
     * Persists a rule. If a rule with the same {@code id} already exists it is replaced;
     * otherwise the rule is added. Returns the saved rule.
     */
    Rule save(Rule rule);

    /**
     * Removes the rule with the given {@code id}.
     * Does nothing if the id is not found.
     */
    void deleteById(String id);

    /**
     * Replaces the entire rule list at once (used internally for batch updates).
     */
    void saveAll(List<Rule> rules);
}
