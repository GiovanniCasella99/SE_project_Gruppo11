package com.unisa.seproject.event;

import org.springframework.context.ApplicationEvent;

/**
 * Published by {@link com.unisa.seproject.service.RuleEvaluationService} each time
 * a rule successfully fires. Listeners (e.g. the SSE controller) can react without
 * coupling to the evaluation service.
 *
 * <p>{@code message} is non-null only when the action is a {@code MessageAction};
 * the frontend uses it to display an alarm popup.
 */
public class RuleFiredEvent extends ApplicationEvent {

    private final String ruleId;
    private final String ruleName;
    private final String firedAt; // ISO-8601
    private final String message; // null for non-message actions

    public RuleFiredEvent(Object source, String ruleId, String ruleName, String firedAt, String message) {
        super(source);
        this.ruleId   = ruleId;
        this.ruleName = ruleName;
        this.firedAt  = firedAt;
        this.message  = message;
    }

    public String getRuleId()   { return ruleId; }
    public String getRuleName() { return ruleName; }
    public String getFiredAt()  { return firedAt; }
    public String getMessage()  { return message; }
}
