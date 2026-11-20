# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.8] - 2026-11-20

### Added

- Antora documentation site with usage guides and code examples.
- Gitflow development: `develop` branch, snapshot publishing, release/sync/security workflows and Dependabot.

### Changed

- CI builds run on JDK 21 (the library still targets JVM 17).
- Updated `irurueta-android-glutils` to 1.1.13 and `irurueta-android-test-utils` to 1.3.3.
  JVM unit tests that use this library (including Robolectric) now need a JDK 21 runtime, because a transitive
  dependency of `irurueta-android-glutils` is compiled for Java 21; the library itself still targets JVM 17.

## [1.1.7] - 2026-03-06

### Changed

- Upgraded build toolchain to Android Gradle Plugin 9.0.1 and Gradle 9.4.0, Kotlin 2.3.10.
- Updated `irurueta-android-glutils` dependency to 1.1.11 and other dependencies.

## [1.1.6] - 2025-12-28

### Changed

- Raised minimum SDK from 24 to 26.
- Updated to Kotlin 2.3.0, `irurueta-android-glutils` 1.1.10 and other dependencies.

## [1.1.5] - 2025-09-24

### Changed

- Updated to Android Gradle Plugin 8.13.0, Kotlin 2.2.20, `irurueta-android-glutils` 1.1.9, Material 1.13.0 and
  other dependencies.

## [1.1.4] - 2025-07-29

### Changed

- Updated `irurueta-android-glutils` dependency to 1.1.8.

## [1.1.3] - 2025-07-27

### Changed

- Updated dependencies and replaced in-repository test helpers with the shared `irurueta-test-utils` library
  (test-only change; no library API changes).

## [1.1.2] - 2025-07-19

### Changed

- Updated to Kotlin 2.2.0, Android Gradle Plugin 8.11.1, Gradle 8.14.3 and `irurueta-android-glutils` 1.1.5.
- Migrated to Kotlin `compilerOptions` and the Maven Central Portal publishing setup.
- Simplified demo app theming.

## [1.1.1] - 2025-06-16

### Changed

- Updated `irurueta-android-glutils` dependency to 1.1.3.

## [1.1.0] - 2025-06-14

### Changed

- Compile and target SDK raised to 36; minimum SDK raised from 21 to 24; Java/Kotlin target is JVM 17.
- Migrated the build to Gradle Kotlin DSL with a version catalog and the Vanniktech Maven Central publishing plugin.
- Completed KDoc for `CubeRenderer` and `CubeTextureView`.
- Updated demo app and test suites.

## [1.0.0] - 2021-12-21

### Added

- Initial release: `CubeTextureView`, an OpenGL ES 2.0 view (based on `GLTextureView`) that draws a 3D cube with
  transparent background support, and its `CubeRenderer`, using `irurueta-android-glutils` to compute camera
  matrices.
- Demo app.

[Unreleased]: https://github.com/albertoirurueta/irurueta-android-gl-cube/compare/1.1.8...HEAD
[1.1.8]: https://github.com/albertoirurueta/irurueta-android-gl-cube/compare/1.1.7...1.1.8
[1.1.7]: https://github.com/albertoirurueta/irurueta-android-gl-cube/compare/1.1.6...1.1.7
[1.1.6]: https://github.com/albertoirurueta/irurueta-android-gl-cube/compare/1.1.5...1.1.6
[1.1.5]: https://github.com/albertoirurueta/irurueta-android-gl-cube/compare/1.1.4...1.1.5
[1.1.4]: https://github.com/albertoirurueta/irurueta-android-gl-cube/compare/1.1.3...1.1.4
[1.1.3]: https://github.com/albertoirurueta/irurueta-android-gl-cube/compare/1.1.2...1.1.3
[1.1.2]: https://github.com/albertoirurueta/irurueta-android-gl-cube/compare/1.1.1...1.1.2
[1.1.1]: https://github.com/albertoirurueta/irurueta-android-gl-cube/compare/1.1.0...1.1.1
[1.1.0]: https://github.com/albertoirurueta/irurueta-android-gl-cube/compare/1.0.0...1.1.0
[1.0.0]: https://github.com/albertoirurueta/irurueta-android-gl-cube/releases/tag/1.0.0
