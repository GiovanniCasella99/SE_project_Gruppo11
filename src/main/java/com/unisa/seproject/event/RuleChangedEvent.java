package com.unisa.seproject.event;

import org.springframework.context.ApplicationEvent;

/**
 * Published by the repository whenever the rule list is modified (create, update, delete).
 *
 * <p><b>Observer pattern</b> via Spring's {@link org.springframework.context.ApplicationEventPublisher}:
 * this replaces the deprecated {@code java.util.Observable} / {@code Observer} used in the
 * old project. Any bean can listen with {@code @EventListener(RuleChangedEvent.class)}
 * without tight coupling to the publisher.
 *
 * <p>The {@link com.unisa.seproject.repository.JsonRuleRepository} listens to this event
 * to persist the current rule list to disk automatically after every change.
 */
public class RuleChangedEvent extends ApplicationEvent {

    public RuleChangedEvent(Object source) {
        super(source);
    }
}
