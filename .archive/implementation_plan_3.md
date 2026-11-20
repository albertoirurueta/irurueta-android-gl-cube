# Implementation plan — Update dependencies

## Task summary

Bump two dependency versions in the Gradle version catalog: `iruruetaTestUtils` 1.3.2 → 1.3.3 and `glutils`
(`irurueta-android-glutils`) 1.1.11 → 1.1.13. Pure version bump, no source changes expected; build and tests confirm
nothing regressed. The changelog and What's new page record the bump, following earlier releases' convention.

Source: GitHub issue #3
Base branch: develop

Open clarifications: none

## Acceptance criteria coverage

| # | Criterion | Covered by tasks |
|---|---|---|
| 1 | "iruruetaTestUtils should be 1.3.3" | Task 1 |
| 2 | "glutils should be 1.1.13" | Task 1 |
| 3 | derived: the library compiles and all unit tests pass with the new versions | Task 2 |
| 4 | derived: the bump is documented in CHANGELOG.md and whats-new.adoc | Task 3 |

## Current code state

- `gradle/libs.versions.toml`: `glutils = "1.1.11"` and `iruruetaTestUtils = "1.3.2"` under `[versions]`. They feed
  `libs.glutils` (an `api` dependency in `lib/build.gradle.kts:108`) and `libs.irurueta.test.utils`.
- Project type: android-library (`lib/` plus Compose sample `app/`), Gradle Kotlin DSL with version catalog.
- `glutils` classes used: `OpenGlToCameraHelper`, `CameraToDisplayOrientation`.
- `CHANGELOG.md` (Keep a Changelog, `## [Unreleased]`) and `docs/modules/ROOT/pages/whats-new.adoc`
  (`== Unreleased`) record dependency bumps.

## Implementation steps

### Group 1 — Bump versions (Parallelizable: yes)

- [x] Task 1. Update the versions in the catalog _(android)_ — versions bumped; no tests added (pure version bump); lib+app tests pass on JDK 21, no new Lint issues.
  - [x] Task 1.1. In `gradle/libs.versions.toml` set `glutils = "1.1.13"`
  - [x] Task 1.2. In `gradle/libs.versions.toml` set `iruruetaTestUtils = "1.3.3"`

### Group 2 — Verify (Parallelizable: yes)

- [x] Task 2. Verify the build and tests with the new versions _(android)_
  - [x] Task 2.1. Delegate to the `iru-gate-runner` agent to run the `lib` unit tests
    (`Skill({skill: "iru-android-test"})`) and confirm the project compiles; if the new `glutils` API changed,
    adapt usages in `lib/src`.
    - Result: no files changed; :lib and :app compile, 105/105 :lib unit tests pass (JDK 21); no new quality issues (no source changes).

### Group 3 — Document (Parallelizable: yes)

- [x] Task 3. Record the bump in the changelog and docs
  - [x] Task 3.1. Add under `### Changed` in the `[Unreleased]` section of `CHANGELOG.md`:
    "Updated `irurueta-android-glutils` to 1.1.13 and `irurueta-android-test-utils` to 1.3.3."
  - [x] Task 3.2. Add a matching `* ⬆️` bullet under `== Unreleased` in
    `docs/modules/ROOT/pages/whats-new.adoc`.
  - Files: CHANGELOG.md, docs/modules/ROOT/pages/whats-new.adoc (docs only; no tests/coverage/quality gates apply).
