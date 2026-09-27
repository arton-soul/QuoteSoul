# Changelog

All notable changes to the **SoulQuote** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.7.0] - 2026-09-27

### Added
- **Phase 5 Ambient & Background Audio System**:
  - `AmbientAudioEngine`: Multi-track concurrent audio engine leveraging AndroidX Media3 `ExoPlayer` instances for gapless looping (`REPEAT_MODE_ONE`) of natural ambient layers.
  - Multi-Channel Audio Mixing:
    - Independent track volume sliders (`0%` to `100%`) with individual toggle switches.
    - Master volume slider scaling all active sounds simultaneously.
    - Master Play/Pause and one-tap "Heningkan Semua" (Mute All).
    - Dynamic animated soundwave bars when a sound is actively playing.
  - Audio Focus & Interruption Management:
    - Android `AudioManager` AudioFocus handling (`USAGE_MEDIA`, `CONTENT_TYPE_MUSIC`).
    - Automatic ducking (volume reduced to 25%) on transient audio focus events (e.g. notifications) with smooth recovery.
    - Automatic pause on complete audio focus loss (e.g. phone calls).
  - Sleep Timer with Smooth Volume Fade-Out:
    - Configurable durations (`15m`, `30m`, `45m`, `60m`, or custom) via modal `SleepTimerDialog`.
    - Live countdown display on the master control bar.
    - Automatic linear 10-second fade-out before playback stops at timer completion.
  - High-Fidelity 100% Offline Ambient Sound Library (`res/raw`):
    - `ambient_rain.wav` ("Hujan Rintik" - soothing rain patter on leaves)
    - `ambient_ocean.wav` ("Ombak Samudra" - rhythmic ocean surf swells)
    - `ambient_forest.wav` ("Hutan Hening" - gentle forest breeze and distant birds)
    - `ambient_campfire.wav` ("Api Unggun" - cozy crackling campfire embers)
    - `ambient_night.wav` ("Malam Syahdu" - peaceful evening crickets and night air)
    - `ambient_zen_bowl.wav` ("Lonceng Tibet" - 432Hz/528Hz pure harmonic singing bowl drone)
    - `ambient_white_noise.wav` ("Derau Lembut" - pink noise for deep focus)
  - Atmospheric Presets (1-tap combinations):
    - "Malam Hujan Hangat" (Rain 75% + Campfire 45%)
    - "Suaka Hutan Zen" (Forest 70% + Zen Bowl 40%)
    - "Tidur Lelap Samudra" (Ocean 65% + Night Crickets 35%)
    - "Fokus & Konsentrasi" (Pink Noise 50% + Rain 40%)
  - Integrated UI & Navigation:
    - Top `TabRow` on the Meditation screen (`Screen.Meditation`): "Panduan Suara" (Guided) and "Suara Alam" (Soundscape Mixer).
    - Quick shortcut card "Soundscape Suara Alam" on `HomeScreen` with direct navigation to the sound mixer.
  - Testing & Verification:
    - Unit tests (`AmbientSoundTest`) verifying library uniqueness, preset references, and timer formatting.
    - All 57 Gradle test tasks passing.
    - Live verification on connected physical Xiaomi Redmi Note 8 Pro (`com.dearyoti.soulquote`).

## [0.6.0] - 2026-09-27

### Added
- **Phase 4 Guided Meditation & Audio Engine**:
  - `MeditationAudioPlayer`: AndroidX Media3 ExoPlayer wrapper with `C.USAGE_MEDIA` and `C.AUDIO_CONTENT_TYPE_SPEECH`, automatic audio focus and ducking, 500ms ticker, seek relative (-15s/+15s), playback speed multiplier (`0.8x`, `1.0x`, `1.2x`), and completion callback.
  - `MeditationDownloadManager`: Robust offline audio downloader with atomic `.download` staging, SHA-256 integrity verification, storage in scoped app internal directory `filesDir/audio/`, and graceful fallback to offline bundled soothing bell audio (`meditation_bell_ambient.wav`).
  - Catalog & Seeding:
    - Added 4 meditation categories (`mindful_breathing`, `inner_calm`, `spiritual_energy`, `deep_sleep`) and 4 guided meditation tracks to `initial_content.json` (version 3).
    - Database seeder extended to automatically populate meditation categories and audio tracks.
  - Domain & Use Cases:
    - `GetMeditationsUseCase`: Dynamic category and search query filtering.
    - `GetMeditationCategoriesUseCase`: Active category retrieval sorted by sort order.
    - `RecordMeditationSessionUseCase`: Room DB logging for completed meditation sessions with completion percentage and duration.
  - Presentation & UI:
    - `MeditationCatalogScreen`: Full-featured catalog with search bar, horizontal category filter chips, session cards with offline indicators, and docked mini-player.
    - `MeditationPlayerDialog`: Modal bottom sheet player featuring mindful breathing pulse circle animation, interactive slider with time formatters, transport controls, and speed selector.
    - 5-Pillar Navigation: Enhanced BottomNav to `Home`, `Explore`, `Meditate`, `Studio`, `Settings`.
    - Prominent Guided Meditation shortcut card and Favorites icon on `HomeScreen`.
  - Testing & Verification:
    - Unit tests (`GetMeditationsUseCaseTest`) covering filtering by category and full catalog fetching.
    - All tests passing (`testDebugUnitTest`, `testReleaseUnitTest`).
    - Verified on physical Xiaomi Redmi Note 8 Pro (`com.dearyoti.soulquote`) with live screenshot confirmation.

## [0.5.0] - 2026-09-27

### Added
- **Phase 6 Quote Studio Engine & Sharing Pipeline**:
  - `QuoteImageExporter`: High-resolution 1080p Bitmap canvas renderer using Android `StaticLayout` with typography wrapping, center-cropped bitmap background scaling, contrast drop shadows, dimmer overlay, and optional branding watermark.
  - Storage & Sharing Pipeline:
    - Scoped storage MediaStore integration saving high-resolution JPEG directly into `Pictures/SoulQuote` without requiring runtime permissions on Android 10+ (API 29+).
    - Android Sharesheet (`ACTION_SEND`) via `FileProvider` (`com.dearyoti.soulquote.fileprovider`) storing temporary shares under app cache `shared_quotes/`.
  - Zero-permission Android Photo Picker (`ActivityResultContracts.PickVisualMedia`) for custom user gallery backgrounds.
  - Domain State & Customizations (`StudioState.kt`):
    - Multi-aspect ratios: `1:1` (Square), `9:16` (Story/Status), `4:5` (Portrait).
    - Presets: Solid mindful palettes and atmospheric linear gradients (Dawn Meditation, Twilight Solitude, Forest Rain, Warm Ochre).
    - Typography: 4 font families (Serif, Sans, Cursive, Monospace), font size slider, 3 text alignments, and color palettes.
    - Visual effects: Dimmer overlay slider (0% to 80%), author toggle, and watermark toggle.
  - UI & Navigation:
    - Dedicated `Quote Studio` tab in bottom navigation bar (`Screen.Studio`).
    - 1-tap "Customize in Studio" palette shortcut buttons on `QuoteCard` and `QuoteDetailDialog` across `HomeScreen`, `ExploreQuotesScreen`, and `FavoritesScreen`.
    - Modal bottom sheet `QuotePickerSheet` to switch quotes directly inside the Studio or roll random quotes.
  - Unit Tests & Build Verification:
    - Verified compile and tests passing with zero errors.
    - Verified on physical Xiaomi Redmi Note 8 Pro (`com.dearyoti.soulquote`).

## [0.4.0] - 2026-09-27

### Added
- **Phase 3 Notification Engine & Reminder Scheduler**:
  - NotificationHelper: Configures high-importance Android Notification Channels (channel_daily_quote_v1, channel_meditation_v1) bound to custom raw audio resources and gentle vibration patterns.
  - Custom local audio chime files in 
es/raw: mindful_bell.wav (528 Hz mindful chime) and zen_singing_bowl.wav (432 Hz resonant singing bowl).
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
