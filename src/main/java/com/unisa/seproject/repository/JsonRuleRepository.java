package com.unisa.seproject.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisa.seproject.event.RuleChangedEvent;
import com.unisa.seproject.model.rule.Rule;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * JSON-backed implementation of {@link RuleRepository}.
 *
 * <p>Rules are kept in an in-memory {@code List} for fast access and persisted to a
 * JSON file on every change. The JSON format is human-readable and version-control
 * friendly — a major improvement over the binary serialisation used in the old project.
 *
 * <p><b>Observer pattern</b>: the repository listens for {@link RuleChangedEvent} (published
 * by itself after each write) and reacts by flushing to disk. This replaces the old
 * {@code SaveThread} that saved synchronously inside every {@code Observable.update()} callback.
 *
 * <p>Thread safety: all mutating operations synchronise on {@code rules} to protect the
 * shared list. Reads obtain a snapshot under the same lock to avoid ConcurrentModificationException
 * in the scheduler thread.
 */
@Repository
public class JsonRuleRepository implements RuleRepository {

    private static final Logger log = LoggerFactory.getLogger(JsonRuleRepository.class);

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final Path storagePath;

    /** In-memory store. All access must synchronise on this field. */
    private final List<Rule> rules = new ArrayList<>();

    public JsonRuleRepository(
            ObjectMapper objectMapper,
            ApplicationEventPublisher eventPublisher,
            @Value("${rules.storage.path:rules.json}") String storagePath) {
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
        this.storagePath = Path.of(storagePath);
    }

    // -------------------------------------------------------------------------
    // Startup: load persisted rules
    // -------------------------------------------------------------------------

    @PostConstruct
    public void load() {
        if (!Files.exists(storagePath)) {
            log.info("No rules file found at '{}', starting with empty list.", storagePath);
            return;
        }
        try {
            List<Rule> loaded = objectMapper.readValue(
                    storagePath.toFile(), new TypeReference<>() {});
            synchronized (rules) {
                rules.addAll(loaded);
            }
            log.info("Loaded {} rule(s) from '{}'.", loaded.size(), storagePath);
        } catch (IOException e) {
            log.error("Failed to load rules from '{}': {}", storagePath, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Observer: persist on any change
    // -------------------------------------------------------------------------

    @EventListener
    public void onRuleChanged(RuleChangedEvent event) {
        persist();
    }

    private void persist() {
        List<Rule> snapshot;
        synchronized (rules) {
            snapshot = new ArrayList<>(rules);
        }
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(storagePath.toFile(), snapshot);
        } catch (IOException e) {
            log.error("Failed to persist rules to '{}': {}", storagePath, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // RuleRepository implementation
    // -------------------------------------------------------------------------

    @Override
    public List<Rule> findAll() {
        synchronized (rules) {
            return Collections.unmodifiableList(new ArrayList<>(rules));
        }
    }

    @Override
    public Optional<Rule> findById(String id) {
        synchronized (rules) {
            return rules.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst();
        }
    }

    @Override
    public Rule save(Rule rule) {
        synchronized (rules) {
            rules.removeIf(r -> r.getId().equals(rule.getId()));
            rules.add(rule);
        }
        eventPublisher.publishEvent(new RuleChangedEvent(this));
        return rule;
    }

    @Override
    public void deleteById(String id) {
        boolean removed;
        synchronized (rules) {
            removed = rules.removeIf(r -> r.getId().equals(id));
        }
        if (removed) {
            eventPublisher.publishEvent(new RuleChangedEvent(this));
        }
    }

    @Override
    public void saveAll(List<Rule> updatedRules) {
        synchronized (rules) {
            rules.clear();
            rules.addAll(updatedRules);
        }
        eventPublisher.publishEvent(new RuleChangedEvent(this));
    }
}
