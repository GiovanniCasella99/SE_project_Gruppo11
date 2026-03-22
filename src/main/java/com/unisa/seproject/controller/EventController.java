package com.unisa.seproject.controller;

import com.unisa.seproject.event.RuleFiredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Streams rule-fired events to connected browser clients via Server-Sent Events (SSE).
 *
 * <p>The frontend opens a persistent GET /api/events connection. Whenever a rule fires,
 * {@link RuleFiredEvent} is published on the Spring event bus and forwarded to every
 * active emitter as a named SSE event ({@code rule-fired}).
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class EventController {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L); // 0 = no timeout
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(()   -> emitters.remove(emitter));
        emitter.onError(e      -> emitters.remove(emitter));
        return emitter;
    }

    @EventListener
    public void onRuleFired(RuleFiredEvent event) {
        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("rule-fired")
                        .data(new FiredPayload(event.getRuleId(), event.getRuleName(), event.getFiredAt(), event.getMessage())));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        emitters.removeAll(dead);
    }

    public record FiredPayload(String ruleId, String ruleName, String firedAt, String message) {}
}
