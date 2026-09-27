# Changelog

All notable changes to the **SoulQuote** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.3.0] - 2026-09-27

### Added
- **Phase 2 Quote System Implementation**.
- Domain Use Cases: `GetDailyQuoteUseCase`, `GetQuotesUseCase`, `GetFavoriteQuotesUseCase`, `ToggleFavoriteUseCase`, `GetRandomQuoteUseCase`, `GetCategoriesUseCase`.
- Jetpack Compose UI Screens:
  - `HomeScreen`: Mindful greeting, Featured Daily Quote Card, category shortcuts, daily reflection prompt.
  - `ExploreQuotesScreen`: Live search filtering (by quote text, author, or tags), category chips, and quote list.
  - `FavoritesScreen`: Bookmarked quotes collection with empty state and quick actions.
  - `SettingsScreen`: Daily quote reminder toggles, appearance preview, and content storage info.
- UI Components:
  - `QuoteCard`: Elegant Material 3 card with serif quote styling, author attribution, category chip, favorite toggle, and sharing.
  - `QuoteDetailDialog`: Modal dialog with complete quote text, source, hashtags, copy to clipboard, and sharesheet integration.
- Navigation: Jetpack Compose NavigationBar with 4 primary tabs (`Home`, `Explore`, `Favorites`, `Settings`) and `SoulQuoteNavGraph`.
- ViewModel: `QuoteViewModel` managing UI state via reactive `StateFlow`.
- Unit tests: `GetDailyQuoteUseCaseTest` verifying daily rotation and random fallback logic.

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
