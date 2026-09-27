# Architecture Decision Records (ADR)

## ADR-001: Adoption of Clean Architecture + MVVM + UDF
- **Date**: 2026-09-27
- **Status**: Accepted
- **Context**: SoulQuote requires an offline-first foundation with independent presentation, business logic, and multiple data sources (local Room database, Google Drive content updates, internal file storage).
- **Decision**: Adopt Clean Architecture with distinct `core`, `data`, `domain`, and `presentation` layers. ViewModels expose state via Kotlin `StateFlow` using Unidirectional Data Flow (UDF).
- **Consequences**: Ensures that business logic is completely decoupled from Android UI and database frameworks, enabling fast unit testing and long-term maintainability.

---

## ADR-002: Database Engine & Content vs. User Data Separation
- **Date**: 2026-09-27
- **Status**: Accepted
- **Context**: The app needs dynamic content updates that can refresh quotes and meditations without wiping out user bookmarks, meditation history, or personalized settings.
- **Decision**: Use Room Database with strictly partitioned tables:
  1. Developer Content: `quotes`, `meditations`, `templates`, etc.
  2. User Data: `favorites`, `meditation_history`, `user_settings`, etc.
- **Consequences**: Content sync operations run within dedicated database transactions touching only developer tables. User data is never deleted or overwritten during updates.

---

## ADR-003: UI Framework Selection — Jetpack Compose & Material 3
- **Date**: 2026-09-27
- **Status**: Accepted
- **Context**: SoulQuote needs a serene, modern, responsive UI with complex canvas rendering capabilities for Quote Studio.
- **Decision**: Use 100% Jetpack Compose with Material 3.
- **Consequences**: Eliminates XML layout overhead, provides native edge-to-edge support, and allows seamless rendering of custom Quote Studio canvases.

---

## ADR-004: Google Drive as Static Distribution Storage
- **Date**: 2026-09-27
- **Status**: Accepted
- **Context**: Developer content updates must be deployable without forcing Play Store APK updates, while avoiding expensive server infrastructure.
- **Decision**: Treat Google Drive as a static file distribution storage (CDN), not as an active realtime database. The app downloads static `manifest.json` and bundled zip packages.
- **Consequences**: Eliminates complex cloud sync code, keeps the app strictly offline-first, and guarantees resilience even if Google Drive links fail or face bandwidth throttling.

---

## ADR-005: Media & Audio Handling via Android Media3
- **Date**: 2026-09-27
- **Status**: Accepted
- **Context**: Guided meditations need background playback, lock screen controls, notification playback controls, and offline audio file handling.
- **Decision**: Use Android Media3 (ExoPlayer) for guided sessions and lightweight looping players for ambient audio.
- **Consequences**: Delivers reliable audio focus management, background playback efficiency, and support for local file URIs.

---

## ADR-006: Physical Database Separation (Content DB vs. User DB)
- **Date**: 2026-09-27
- **Status**: Accepted
- **Context**: SoulQuote requires seamless content distribution (quotes, meditations, templates) updated dynamically via Google Drive without any risk of altering, corrupting, or wiping user personal data (favorites, meditation history, settings).
- **Decision**: Create two separate physical SQLite Room databases:
  1. `SoulQuoteContentDatabase` (`soulquote_content.db`): Contains developer content (`quotes`, `quote_categories`, `meditations`, `templates`, `app_content_config`).
  2. `SoulQuoteUserDatabase` (`soulquote_user.db`): Contains user personal data (`favorites`, `meditation_history`, `user_settings`, `downloaded_audio`).
- **Consequences**: Physical file-level isolation guarantees that content updates, file replacements, or rollbacks can never touch or corrupt user data. Repositories coordinate queries across DAOs seamlessly using reactive Kotlin Coroutines & Flow.
