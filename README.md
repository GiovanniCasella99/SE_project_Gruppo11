# SE Project – Backend Documentation

## Overview

Rule-based automation engine built with **Spring Boot** and **Java 17**.
The backend exposes a REST API consumed by the [React frontend (FE_SE_Project_Gruppo11)](https://github.com/GiovanniCasella99/FE_SE_Project_Gruppo11) and runs a background scheduler that periodically evaluates rules and executes their actions when the configured conditions are met.

---

## Technology Stack

| | |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.4 |
| Build tool | Maven (wrapper included) |
| Serialization | Jackson (JSON) |
| Persistence | JSON flat file (`rules.json`) |
| Tests | JUnit 5 / Spring Boot Test |

---

## Running the application

```bash
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080`.
A `rules.json` file is created in the working directory the first time a rule is saved.

### Configuration (`application.properties`)

| Property | Default | Description |
|---|---|---|
| `rules.storage.path` | `rules.json` | Path to the JSON persistence file |
| `rule.check.interval` | `10000` | Rule evaluation interval in milliseconds |

---

## Package Structure

```
src/main/java/com/unisa/seproject/
│
├── SeprojectApplication.java          Entry point (@SpringBootApplication + @EnableScheduling)
│
├── configuration/
│   └── JacksonConfig.java             ObjectMapper bean with JavaTimeModule
│
├── model/
│   ├── rule/
│   │   ├── Rule.java                  Domain entity (id, name, trigger, action, active, firingMode, …)
│   │   └── FiringMode.java            Enum: FIRE_MULTIPLE | FIRE_ONCE | SLEEP
│   │
│   ├── trigger/
│   │   ├── Trigger.java               Sealed interface – boolean isVerified()
│   │   ├── TimeTrigger.java           Fires at a specific wall-clock time (hh:mm)
│   │   ├── DayOfWeekTrigger.java      Fires on a specific day of the week
│   │   ├── DayOfMonthTrigger.java     Fires on a specific day of the month
│   │   ├── DayOfYearTrigger.java      Fires every year on the same month/day
│   │   ├── FileSizeTrigger.java       Fires when a file reaches a size threshold (KB)
│   │   └── FileInDirectoryTrigger.java Fires when a named file appears in a directory
│   │
│   └── action/
│       ├── Action.java                Sealed interface – void execute()
│       ├── FileOperation.java         Enum: COPY | MOVE | REMOVE
│       ├── MessageAction.java         Logs a message (SLF4J)
│       ├── AudioAction.java           Plays a WAV file on the host machine
│       ├── FileAction.java            Copies / moves / removes a file
│       ├── FileAppendAction.java      Appends text to a file (creates it if absent)
│       ├── ExecuteProgramAction.java  Launches an external program
│       └── CompositeAction.java       Executes multiple actions sequentially (Composite pattern)
│
├── engine/
│   └── strategy/
│       ├── FiringStrategy.java        Interface: canFire(rule) + onFired(rule)
│       ├── FireMultipleStrategy.java  Always allows firing (default)
│       ├── FireOnceStrategy.java      Allows firing only if rule has never fired before
│       ├── SleepStrategy.java         Allows firing after a cooldown period has elapsed
│       └── FiringStrategyFactory.java Maps FiringMode enum → FiringStrategy instance
│
├── event/
│   ├── RuleChangedEvent.java          Spring ApplicationEvent published on any rule mutation
│   └── RuleFiredEvent.java            Spring ApplicationEvent published after each rule firing (carries ruleId, ruleName, firedAt, message)
│
├── repository/
│   ├── RuleRepository.java            Interface: findAll / findById / save / deleteById / saveAll
│   └── JsonRuleRepository.java        JSON-backed impl; auto-saves on RuleChangedEvent
│
├── service/
│   ├── RuleService.java               CRUD operations + toggleActive
│   └── RuleEvaluationService.java     Evaluation loop: checks triggers and fires actions
│
├── scheduler/
│   └── RuleScheduler.java             @Scheduled wrapper around RuleEvaluationService
│
└── controller/
    ├── RuleController.java            REST controller – /api/rules
    ├── EventController.java           SSE endpoint – /api/events (pushes RuleFiredEvent to connected browsers)
    └── FileBrowserController.java     REST controller – /api/fs/programs (scans Windows Store apps)
```

---

## Domain Model

### Rule

The central entity. A rule binds a **Trigger** to an **Action** and controls how and when the action repeats.

| Field | Type | Description |
|---|---|---|
| `id` | `String` (UUID) | Assigned on creation, never changed |
| `name` | `String` | Human-readable label |
| `trigger` | `Trigger` | Condition to evaluate |
| `action` | `Action` | What to do when the trigger fires |
| `active` | `boolean` | If `false`, the rule is skipped entirely |
| `firingMode` | `FiringMode` | Controls re-firing behaviour |
| `sleepMinutes` | `long` | Cooldown (only meaningful for `SLEEP` mode) |
| `lastFiredAt` | `LocalDateTime` | Set by the engine after each firing; `null` = never fired |

### FiringMode

| Value | Behaviour |
|---|---|
| `FIRE_MULTIPLE` | Fires every evaluation cycle in which the trigger is satisfied *(default)* |
| `FIRE_ONCE` | Fires exactly once; ignored forever after `lastFiredAt` is set |
| `SLEEP` | Fires, then waits `sleepMinutes` before being eligible again |

---

## Triggers

All triggers are immutable **records** implementing the sealed interface `Trigger`.
Jackson uses the `"type"` field to deserialise the correct implementation.

| Type name | Record fields | Condition |
|---|---|---|
| `TIME` | `time: LocalTime` | Current time (hh:mm) equals `time` |
| `DAY_OF_WEEK` | `dayOfWeek: DayOfWeek` | Today is `dayOfWeek` |
| `DAY_OF_MONTH` | `day: int` | Today's day-of-month equals `day` |
| `DAY_OF_YEAR` | `date: LocalDate` | Today's month+day equals `date`'s month+day (annual) |
| `FILE_SIZE` | `filePath: String`, `thresholdKb: long` | File size ≥ `thresholdKb` KB |
| `FILE_IN_DIRECTORY` | `directoryPath: String`, `fileName: String` | File named `fileName` exists in `directoryPath` |

---

## Actions

All leaf actions are immutable **records** implementing the sealed interface `Action`.

| Type name | Record fields | Effect |
|---|---|---|
| `MESSAGE` | `message: String` | Logs the message at INFO level |
| `AUDIO` | `filePath: String` | Plays a WAV file asynchronously on the host |
| `FILE` | `sourcePath`, `destinationPath`, `operation: FileOperation` | Copies, moves, or deletes a file |
| `FILE_APPEND` | `filePath: String`, `content: String` | Appends text to a file; creates it if absent |
| `EXECUTE_PROGRAM` | `programPath: String`, `args: List<String>` | Launches an external process (fire-and-forget) |
| `COMPOSITE` | `actions: List<Action>` | Executes a list of actions in order *(Composite pattern)* |

`FileOperation` values: `COPY`, `MOVE`, `REMOVE`.

---

## Evaluation Engine

Every `rule.check.interval` milliseconds, `RuleScheduler` calls `RuleEvaluationService.evaluateAll()`.

For each rule the following steps are applied in order:

```
1. rule.isActive()          → false  →  skip
2. trigger != null
   action  != null          → false  →  skip
3. trigger.isVerified()     → false  →  skip
4. FiringStrategyFactory
   .forMode(firingMode)
   .canFire(rule)           → false  →  skip
5. action.execute()
6. strategy.onFired(rule)         (updates rule.lastFiredAt)
7. repository.save(rule)          (persists lastFiredAt to JSON)
```

---

## Design Patterns

### Strategy — `FiringStrategy`

Controls *when* a rule may fire again. Each `FiringMode` value maps to a stateless strategy instance held in `FiringStrategyFactory`.

```
FiringStrategy
├── FireMultipleStrategy   canFire → always true
├── FireOnceStrategy       canFire → lastFiredAt == null
└── SleepStrategy          canFire → lastFiredAt == null
                                     || now >= lastFiredAt + sleepMinutes
```

Adding a new firing behaviour = add one class + one entry in the factory map.

### Composite — `CompositeAction`

Groups an ordered list of `Action`s and executes them as a single unit.
A failure in one action does not prevent the rest from running.

```json
{
  "type": "COMPOSITE",
  "actions": [
    { "type": "MESSAGE",     "message": "Starting backup" },
    { "type": "FILE",        "sourcePath": "/data", "destinationPath": "/backup", "operation": "COPY" },
    { "type": "FILE_APPEND", "filePath": "/log.txt", "content": "Backup done\n" }
  ]
}
```

### Repository — `RuleRepository`

Abstracts all data access behind an interface. The current implementation (`JsonRuleRepository`) reads/writes a JSON file. Switching to a database only requires a new implementation; the service layer is unchanged.

### Observer — Spring `ApplicationEventPublisher`

Two events flow through the application:

- `RuleChangedEvent` — published by `JsonRuleRepository` after every mutation; the repository also listens to it and flushes the rule list to disk. Replaces the deprecated `java.util.Observable` / `Observer` from the old project.
- `RuleFiredEvent` — published by `RuleEvaluationService` after each successful rule firing; consumed by `EventController` which pushes the payload to all connected SSE clients.

### Factory — `FiringStrategyFactory`

Maps each `FiringMode` enum value to its `FiringStrategy` implementation.
Jackson's `@JsonSubTypes` acts as a factory for polymorphic trigger/action deserialization.

---

## REST API

`@CrossOrigin` is configured for `http://localhost:3000` and `http://localhost:5173` (Vite dev server).

### Rules – `/api/rules`

| Method | Path | Description | Body | Response |
|---|---|---|---|---|
| `GET` | `/api/rules` | List all rules | — | `200 List<Rule>` |
| `GET` | `/api/rules/{id}` | Get one rule | — | `200 Rule` / `404` |
| `POST` | `/api/rules` | Create a rule | `Rule` (no id needed) | `201 Rule` |
| `PUT` | `/api/rules/{id}` | Replace a rule | `Rule` | `200 Rule` / `404` |
| `DELETE` | `/api/rules/{id}` | Delete a rule | — | `204` / `404` |
| `PATCH` | `/api/rules/{id}/toggle` | Toggle active/inactive | — | `200 Rule` / `404` |

### Server-Sent Events – `/api/events`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/events` | SSE stream; emits a `RuleFiredEvent` JSON payload every time a rule fires |

**Payload shape:**
```json
{ "ruleId": "...", "ruleName": "Morning reminder", "firedAt": "2026-03-22T09:00:00", "message": "Good morning!" }
```
`message` is `null` for non-MESSAGE actions. The React frontend subscribes via `EventSource` and shows a popup for alarms.

### Program browser – `/api/fs`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/fs/programs` | Returns a sorted list of `{name, path}` entries for installed Microsoft Store apps |

Scans `%USERPROFILE%\AppData\Local\Microsoft\WindowsApps` (depth 3, `.exe` files only). Used by the frontend's program picker.

### Example: create a rule

```json
POST /api/rules
{
  "name": "Daily reminder",
  "firingMode": "FIRE_MULTIPLE",
  "trigger": {
    "type": "TIME",
    "time": "09:00"
  },
  "action": {
    "type": "MESSAGE",
    "message": "Good morning!"
  }
}
```

### Example: rule with sleep cooldown

```json
POST /api/rules
{
  "name": "Big file alert",
  "firingMode": "SLEEP",
  "sleepMinutes": 60,
  "trigger": {
    "type": "FILE_SIZE",
    "filePath": "/var/log/app.log",
    "thresholdKb": 10240
  },
  "action": {
    "type": "FILE_APPEND",
    "filePath": "/var/log/alerts.txt",
    "content": "app.log exceeded 10 MB\n"
  }
}
```

---

## Persistence

Rules are stored in a single JSON file (default: `rules.json` in the working directory).
The file is written automatically after every create/update/delete operation.
On startup, `JsonRuleRepository` loads the file via `@PostConstruct`.

The JSON is human-readable, diff-friendly, and easy to edit manually.

```json
[ {
  "id" : "a1b2c3d4-...",
  "name" : "Daily reminder",
  "trigger" : { "type" : "TIME", "time" : "09:00" },
  "action"  : { "type" : "MESSAGE", "message" : "Good morning!" },
  "active" : true,
  "firingMode" : "FIRE_MULTIPLE",
  "sleepMinutes" : 0,
  "lastFiredAt" : "2026-03-22T09:00:00"
} ]
```

---

