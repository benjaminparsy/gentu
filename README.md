# Gentu

[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=benjaminparsy_gentu&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=benjaminparsy_gentu)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=benjaminparsy_gentu&metric=coverage)](https://sonarcloud.io/summary/new_code?id=benjaminparsy_gentu)

**Gentu** is a Java library that automatically generates human-readable unit test reports from annotations placed directly on your JUnit 5 test methods. After your test suite runs, a structured `.txt` report is produced in `target/gentu/`, documenting each test's name, description, preconditions, and expected outcomes — with zero manual effort.

---

## Table of Contents

- [Why Gentu?](#why-gentu)
- [Features](#features)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Annotations Reference](#annotations-reference)
- [Report Output](#report-output)
- [File Attachments](#file-attachments)
- [Display Name Integration](#display-name-integration)
- [How It Works](#how-it-works)

---

## Why Gentu?

Keeping test documentation in sync with the actual test code is a constant challenge. Tests evolve, documentation doesn't — and soon no one trusts either.

Gentu solves this by **making your annotations the single source of truth**. You describe your test once, directly on the method, and Gentu takes care of producing the report every time your tests run.

---

## Features

- **Annotation-driven** — describe tests with `@TestDescriptor`, `@Given`, `@Expected`, `@Description`
- **Zero configuration** — `TestReportListener` is auto-registered via Java's `ServiceLoader` (`META-INF/services`)
- **JUnit 5 native** — integrates as an `AfterTestExecutionCallback` extension and a `TestExecutionListener`
- **File attachments** — reference input files (classpath or absolute path) and Gentu copies them alongside the report
- **Custom display names** — `TestDescriptorNameGenerator` uses `@TestDescriptor.testName()` as the JUnit display name
- **Failed tests excluded** — only passing tests are documented, keeping the report clean

---

## Project Structure

```
gentu/
├── gentu-core/        # Annotations, DTOs, report generation engine
└── gentu-junit5/      # JUnit 5 extension, listener, display name generator
```

| Module | Role |
|---|---|
| `gentu-core` | Defines all annotations, the `TestResult` record, file writing (`TestReportTextFile`), and file copying (`TestReportFileDownloaderImpl`) |
| `gentu-junit5` | Provides the JUnit 5 `TestDescriptorExtension`, `TestReportListener`, and `TestDescriptorNameGenerator` |

---

## Requirements

- Java 21+
- Maven 3.8+
- JUnit Jupiter 5.x

---

## Installation

Add the following dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.benjamin.parsy</groupId>
    <artifactId>gentu-junit5</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

> `gentu-junit5` transitively pulls in `gentu-core`. No additional dependencies are needed.

The `TestReportListener` is registered automatically via `META-INF/services` — the JUnit Platform Launcher picks it up without any configuration.

---

## Quick Start

### 1. Register the extension on your test class

```java
import com.benjamin.parsy.gentu.junit5.TestDescriptorExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestDescriptorExtension.class)
class OrderServiceTest {
    // ...
}
```

### 2. Annotate your test methods

```java
import com.benjamin.parsy.gentu.core.annotation.*;

@ExtendWith(TestDescriptorExtension.class)
class OrderServiceTest {

    @Test
    @TestDescriptor(
        testName = "Create order with valid product — should return order with PENDING status",
        description = @Description("Verifies that creating an order with a valid product sets its status to PENDING"),
        given = @Given(
            value = {
                "A registered customer with ID 42",
                "A product in stock with ID 7"
            }
        ),
        expected = @Expected({
            "Order is created successfully",
            "Order status is PENDING",
            "Order contains exactly one item"
        })
    )
    void createOrder_withValidProduct_shouldReturnPendingOrder() {
        // your test logic here
    }
}
```

### 3. Run your tests

```bash
mvn test
```

The report is generated automatically at:

```
target/gentu/test-report_20260715_143022.txt
```

---

## Annotations Reference

### `@TestDescriptor`

The main annotation, placed on a test method. Aggregates all documentation for a single test case.

| Attribute | Type | Required | Description |
|---|---|---|---|
| `testName` | `String` | Yes | Human-readable name of the test case |
| `description` | `@Description` | Yes | What the test validates |
| `given` | `@Given` | Yes | Preconditions and input data |
| `expected` | `@Expected` | Yes | Expected outcomes |

---

### `@Description`

Describes the intent of the test in a single sentence.

```java
description = @Description("Verifies that an expired token is rejected with a 401 response")
```

---

### `@Given`

Declares the preconditions and inputs for the test. Supports plain text values and file references.

| Attribute | Type | Default | Description |
|---|---|---|---|
| `value` | `String[]` | `""` | Text descriptions of inputs or state |
| `file` | `@File[]` | `{}` | Files used as input (see [File Attachments](#file-attachments)) |

```java
given = @Given(
    value = {"User is authenticated", "Cart contains 3 items"},
    file = @File(path = "data/cart.json", inClasspath = true)
)
```

---

### `@Expected`

Lists the expected outcomes of the test.

```java
expected = @Expected({
    "HTTP response status is 200",
    "Response body contains the order ID",
    "An email confirmation is sent"
})
```

---

### `@File`

References a file used as test input. Gentu copies it to `target/gentu/test_<id>/` alongside the report.

| Attribute | Type | Default | Description |
|---|---|---|---|
| `path` | `String` | — | Path to the file |
| `inClasspath` | `boolean` | `false` | `true` to resolve from the classpath, `false` for an absolute path |

```java
// From classpath (e.g. src/test/resources/payload.json)
@File(path = "payload.json", inClasspath = true)

// From absolute path
@File(path = "/tmp/input/data.csv")
```

---

## Report Output

After a test run, the generated report looks like this:

```
========== TEST REPORT ==========
Generated on : 2026-07-15T14:30:22
Number of tests : 2
============================================================

Summary :

[1] Create order with valid product — should return order with PENDING status
[2] Cancel order — should set status to CANCELLED

============================================================

[1] Create order with valid product — should return order with PENDING status
    Class: OrderServiceTest#createOrder_withValidProduct_shouldReturnPendingOrder
    Description: Verifies that creating an order with a valid product sets its status to PENDING
    Given:
        A registered customer with ID 42
        A product in stock with ID 7
    Expected:
        Order is created successfully
        Order status is PENDING
        Order contains exactly one item
    Executed at: 2026-07-15T14:30:21

------------------------------------------------------------

[2] Cancel order — should set status to CANCELLED
    ...

============================================================
```

The report file is named `test-report_<timestamp>.txt` and placed in `target/gentu/`.

> **Note:** Only **passing** tests are included in the report. Tests that throw an exception are skipped.

---

## File Attachments

When `@Given` references files via `@File`, Gentu copies each file into a dedicated sub-directory next to the report:

```
target/gentu/
├── test-report_20260715_143022.txt
├── test_1/
│   └── cart.json
└── test_2/
    └── payload.xml
```

This makes the report self-contained and reproducible.

---

## Display Name Integration

`TestDescriptorNameGenerator` replaces the default JUnit method name with `@TestDescriptor.testName()` in test reports and IDE output.

Enable it at the class or package level:

```java
import com.benjamin.parsy.gentu.junit5.TestDescriptorNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;

@DisplayNameGeneration(TestDescriptorNameGenerator.class)
@ExtendWith(TestDescriptorExtension.class)
class OrderServiceTest {
    // test names in JUnit output will use @TestDescriptor.testName()
}
```

Or globally via `src/test/resources/junit-platform.properties`:

```properties
junit.jupiter.displayname.generator.default=com.benjamin.parsy.gentu.junit5.TestDescriptorNameGenerator
```

---

## How It Works

```
Test run starts
      │
      ▼
TestReportListener.testPlanExecutionStarted()   ← clears the registry
      │
      │  [for each test method]
      ▼
TestDescriptorExtension.afterTestExecution()    ← reads @TestDescriptor, skips failed tests
      │                                            builds a TestResult, adds it to the registry
      │
      ▼
TestReportListener.testPlanExecutionFinished()  ← retrieves all TestResult from the registry
      │
      ▼
GentuReporterFactory.create("target")
      │
      ▼
GentuReporter.executeReporter()
      │
      ├──► TestReportTextFile.writeReport()     ← writes target/gentu/test-report_<ts>.txt
      │
      └──► TestReportFileDownloaderImpl         ← copies @File attachments to target/gentu/test_<id>/
```
