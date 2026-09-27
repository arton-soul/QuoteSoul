# Changelog

All notable changes to the **SoulQuote** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.4.0] - 2026-09-27

### Added
- **Phase 3 Notification Engine & Reminder Scheduler**:
  - NotificationHelper: Configures high-importance Android Notification Channels (channel_daily_quote_v1, channel_meditation_v1) bound to custom raw audio resources and gentle vibration patterns.
  - Custom local audio chime files in es/raw: mindful_bell.wav (528 Hz mindful chime) and zen_singing_bowl.wav (432 Hz resonant singing bowl).
  - NotificationScheduler: Alarm scheduling service utilizing AlarmManager.setExactAndAllowWhileIdle (with graceful inexact fallback for battery preservation and Android 13+/14+ restrictions).
  - Broadcast Receivers:
    - DailyQuoteNotificationReceiver: Resolves today's inspirational quote from QuoteRepository and delivers BigTextStyle notification.
    - MeditationReminderReceiver: Fires mindful meditation reminders and automatically rolls over to the next day.
    - BootReceiver: Reschedules active alarms on device reboot (BOOT_COMPLETED, MY_PACKAGE_REPLACED, QUICKBOOT_POWERON).
  - Interactive SettingsScreen UI:
    - Switches for Daily Quote and Meditation Reminder.
    - Material 3 TimePickerDialog integration for customizable notification triggers.
    - Dedicated audio and notification test buttons (Test Bell, Test Bowl).
  - Unit tests: NotificationSchedulerTest testing next trigger calculations and 24-hour rollover logic.

### Verified
- Built and verified APK on connected physical device Xiaomi Redmi Note 8 Pro (egonia).
- Verified Notification Channels, audio playback, BigTextStyle notification display, and Settings UI via live ADB broadcast and log dump inspection.


## [0.3.1] - 2026-09-27

### Added
- Ingested 60 inspirational quotes by Bunda Arsaningsih from `quotesoul.md` with attribution and new category `jiwa_spiritual` ("Jiwa & Energi"). Total quote seed expanded to 72 quotes across 5 categories.
- Upgraded `DatabaseSeeder` with incremental version checking (`content_version` config comparison) for non-destructive seed updates.

### Changed
- Updated `applicationId` to `com.dearyoti.soulquote`.
- Migrated annotation processing from KAPT to KSP (`com.google.devtools.ksp:2.2.21-2.0.5`) for modern, robust Kotlin 2.2.21 Room 2.8.4 code generation.

### Verified
- Built and installed debug APK to physical device Xiaomi Redmi Note 8 Pro (`begonia`).
- Verified on-device database seeding and Jetpack Compose UI rendering without crashes.

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
