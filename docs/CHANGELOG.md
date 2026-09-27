# Changelog

All notable changes to the **SoulQuote** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] - 2026-09-27

### Added
- **Phase 1 Local Database Implementation** with physical separation between Developer Content and User Data.
- `SoulQuoteContentDatabase` (`soulquote_content.db`) containing `quotes`, `quote_categories`, `meditations`, `meditation_categories`, `templates`, and `app_content_config`.
- `SoulQuoteUserDatabase` (`soulquote_user.db`) containing `favorites`, `meditation_history`, `user_settings`, and `downloaded_audio`.
- Reactive Room DAOs (`QuoteDao`, `MeditationDao`, `TemplateDao`, `FavoriteDao`, `UserSettingDao`, `MeditationHistoryDao`, `DownloadedAudioDao`).
- Entity-to-Domain mappers and pure domain models (`Quote`, `QuoteCategory`, `Meditation`, `QuoteTemplate`, `UserSettings`, `MeditationHistoryItem`).
- Repositories: `QuoteRepositoryImpl` (with deterministic daily quote calculation and Flow-based favorite composition), `MeditationRepositoryImpl`, `UserRepositoryImpl`.
- Bundled seed assets (`initial_content.json`) and `DatabaseSeeder` for initial offline launch.
- `AppContainer` service locator dependency injection in `SoulQuoteApp`.
- Unit tests for repository layers: `QuoteRepositoryTest` and `UserRepositoryTest`.
- Architectural Decision Record `ADR-006` for physical database separation.

## [0.1.0] - 2026-09-27

### Added
- Phase 0 Project Foundation established.
- Gradle build setup with Kotlin DSL, Android Gradle Plugin 8.13.2, Kotlin 2.2.21, and Room 2.8.4.
- Clean Architecture modular package layout (`core`, `data`, `domain`, `presentation`).
- Material 3 theme with mindful color palette (`SoulQuoteTheme`).
- AndroidManifest configured with launcher activity and edge-to-edge Compose entrypoint.
- Core utilities including `Resource<T>` result wrapper.
- Baseline unit test verification suite.
- Comprehensive technical documentation suite under `/docs` (Master Plan, Architecture, Database, Content System, Audio, Notification, Studio, Google Drive, Security, Testing, Release, Decisions, Progress).
