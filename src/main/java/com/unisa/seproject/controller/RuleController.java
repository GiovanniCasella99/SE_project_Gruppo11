package com.unisa.seproject.controller;

import com.unisa.seproject.model.rule.Rule;
import com.unisa.seproject.service.RuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for managing automation rules.
 *
 * <p>All endpoints are under {@code /api/rules}. {@code @CrossOrigin} allows the React
 * dev server (default port 3000) to call this API during development; configure a proper
 * reverse proxy for production.
 *
 * <pre>
 * GET    /api/rules          – list all rules
 * GET    /api/rules/{id}     – get a single rule
 * POST   /api/rules          – create a new rule
 * PUT    /api/rules/{id}     – replace an existing rule
 * DELETE /api/rules/{id}     – delete a rule
 * PATCH  /api/rules/{id}/toggle – toggle active / inactive
 * </pre>
 */
@RestController
@RequestMapping("/api/rules")
@CrossOrigin(origins = "http://localhost:3000")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping
    public List<Rule> getAll() {
        return ruleService.getAllRules();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rule> getById(@PathVariable String id) {
        return ruleService.getRuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Rule> create(@RequestBody Rule rule) {
        Rule created = ruleService.createRule(rule);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rule> update(@PathVariable String id, @RequestBody Rule rule) {
        return ruleService.updateRule(id, rule)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return ruleService.deleteRule(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Rule> toggle(@PathVariable String id) {
        return ruleService.toggleActive(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
