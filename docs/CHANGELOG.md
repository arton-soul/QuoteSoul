# Changelog

All notable changes to the **SoulQuote** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.3.0] - 2026-09-28

### Fixed & Enhanced
- **Data Backup & Privacy Synchronization**:
  - Connected backup and privacy statistics to a real-time reactive Room Flow (`getUserDataStatsFlow()`), combining favorites, meditation history, journal reflections, and settings.
  - Corrected counter display in `SettingsScreen` to show: `${favoritesCount} Favorit • ${historyCount} Riwayat • ${journalCount} Refleksi`.
  - Added automatic stat refresh whenever navigating to or resuming the Settings screen.
- **Mindful Bell & Zen Singing Bowl Audio Testing**:
  - Implemented direct in-app audio playback via `MediaPlayer` (`playBellSound()` and `playZenBowlSound()`) ensuring instant, audible feedback when pressing "Uji Lonceng" and "Uji Mangkuk Zen" without relying solely on system notification sounds.
  - Upgraded notification channels to `v2` (`channel_daily_quote_v2` and `channel_meditation_v2`) with `IMPORTANCE_HIGH` and universal resource URIs for reliable ringtone delivery.
- **Reminder & Notification Controls**:
  - Added interactive bilingual Toast feedback for toggling Daily Quote reminders, Meditation reminders, Gentle vibration, and updating scheduled reminder times.
  - Added `PendingPermissionAction` workflow ensuring that when Android 13+ `POST_NOTIFICATIONS` permission is requested, the pending test sound/notification executes seamlessly immediately after being granted.
- **Physical Device Validation**:
  - Full end-to-end installation, soundscape playback, notification triggering, and database counter synchronization validated on physical Xiaomi Redmi device.
- **Content Update v8 (OTA & Seed)**:
  - Published Content Package Version 8 containing 60 new wisdom quotes and soul reflections on *Misteri Doa, Kepasrahan, & Hukum Semesta* by Bunda Arsaningsih & dr. Rastho Mahotama.
  - Merged into `initial_content.json` (increasing total offline quotes to 192) and deployed live via `distribution/content_update_v8.json` and `distribution/content_manifest.json`.
- **Content Update v7 (OTA & Seed)**:
  - Published Content Package Version 7 containing 60 new quotes by Bunda Arsaningsih from the podcast series *Karma, Reinkarnasi, & Misi Jiwa* (Channeling & Energi, Spiritual Gift, Misi Jiwa).
  - Merged into `initial_content.json` (increasing bundled offline quotes to 132) and deployed live via `distribution/content_update_v7.json` and `distribution/content_manifest.json`.

## [0.2.0] - 2026-09-28

### Added
- **Multi-Language Support (Indonesian & English)**:
  - Added full bilingual localization engine (`AppStrings`, `IndonesianStrings`, `EnglishStrings`).
  - Added Language Selection in `SettingsScreen` (Bahasa Indonesia / English) with instantaneous UI reactivity.
  - Localized all screens: Home, Meditation Catalog, Quote Studio, Favorites, History, Journal, Badges, and Settings.
  - Added localized Android system string resources (`values` & `values-en`).
- **Play Store Branding Assets & Graphics**:
  - Designed and generated high-resolution 512x512 app icon combining mindful lotus/quote with an elegant heart motif.
  - Generated 1024x500 Feature Graphic banner for Google Play Store listing.
  - Generated adaptive launcher icons (`mipmap-anydpi-v26`, `hdpi`, `mdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`).

### Fixed
- **Mindful Journal & Reflection**:
  - Fixed multiple reflections per day issue where new entries previously overwrote earlier entries due to key collisions.
  - Supported multiple daily reflection entries with distinct IDs, real-time list display, time badges, editing, and deletion.
  - Redesigned mood selector into a 2-row x 3-column balanced grid, eliminating awkward vertical text-wrapping and stretching for "Gelisah" and other moods.
- **Audio Meditasi & Offline Management**:
  - Ensured seamless Google Drive streaming fallback and background download caching.
  - Fixed deletion state tracking and checklist icon updates in catalog.

## [0.12.0] - 2026-09-27

### Added
- **Phase 11 Quality Assurance & Hardening**:
  - Expanded Unit Test Suite:
    - 13 comprehensive JVM unit test suites spanning architecture layers (`core`, `data`, `domain`, `presentation`).
    - Added `MeditationDownloadManagerTest` verifying cryptographic SHA-256 integrity, corruption detection, byte-flipping resilience, and staging cleanup.
    - Added `RoomMigrationTest` validating dynamic SQLite schema generation and indexing for `MIGRATION_1_2`.
    - Added `QuoteStudioConfigTest` testing aspect ratios (`1:1`, `9:16`, `4:5`), color palettes, fonts, and overlay bounds.
    - All 13 test suites passing 100% on JVM in 53 seconds.
  - Offline-First & Airplane Mode Hardening:
    - Physical verification on Xiaomi Redmi Note 8 Pro under active Airplane Mode.
    - Zero network dependency: Daily Quotes, Room local database queries, soundscape audio, and mindful reflections operate flawlessly offline.
    - Instant offline recovery and persistence verified.

## [0.11.0] - 2026-09-27

### Added
- **Phase 10 Modern Features & Enhancements**:
  - `MindfulStreak` & Achievement Badges:
    - Automatic calculation of current consecutive practice streak and all-time record streak based on local meditation and journaling events.
    - 6 unlockable Mindful Badges (*Langkah Pertama*, *Fokus 3 Hari*, *Seminggu Damai*, *Kedamaian Batin*, *Zen Master*, *Penyelam Batin*) with visual progress bars.
    - Streak badge counter pill (`🔥 X Hari`) embedded on `HomeScreen` header with interactive `MindfulBadgesDialog`.
  - Daily Mindful Journaling & Mood Tracking:
    - Room User Database expansion: added `JournalEntryEntity`, `JournalDao`, and safe `MIGRATION_1_2` without data loss.
    - 6 core emotional moods (`PEACEFUL`, `GRATEFUL`, `CALM`, `ENERGETIC`, `TIRED`, `ANXIOUS`) with emoji avatars.
    - Timezone and hour-aware reflection prompts (morning inspiration, afternoon pause, evening gratitude, night letting go) with random prompt generator.
    - Interactive `MindfulJournalDialog` supporting two tabs: reflection entry and history log with instant delete.
    - Full inclusion of journal entries into `UserDataBackupManager` JSON backup and restore routines.
  - Android AppWidget (Daily Quote Widget):
    - Added `DailyQuoteWidgetProvider` (4x2 widget format) displaying the current daily quote, author attribution, and category tag.
    - Tap widget card to launch SoulQuote directly via deep link.
    - Tap refresh icon on widget to immediately cycle to an inspiring random quote.
    - Automated widget synchronization upon daily quote notification broadcast.
  - Deep Linking Support:
    - Added intent-filters for custom scheme `soulquote://` (`quote/{id}`, `meditation/{id}`, `studio?quoteId={id}`, `journal`) and verified web domain `https://soulquote.app`.
    - Deep links verified and tested via ADB on physical Xiaomi Redmi Note 8 Pro.

## [0.10.0] - 2026-09-27

### Added
- **Phase 9 User Data Backup & Privacy**:
  - `UserDataBackupManager`:
    - Full export of user private data: bookmarks/favorites, meditation history, and user preferences into timestamped JSON files (`soulquote_backup_YYYYMMDD_HHmmss.json`).
    - Embedded cryptographic SHA-256 integrity checksum for tamper prevention.
    - Zero telemetry and 100% on-device private data isolation: only queries `soulquote_user.db`, completely avoiding `soulquote_content.db`.
    - Android Sharesheet integration: file generated in isolated `cacheDir/backup/` shared via `FileProvider` (`ACTION_SEND`) without demanding broad storage permissions.
    - Full restore mechanism: JSON parsing, integrity validation, version/schema check, and atomic database replacement inside `userDatabase.withTransaction`.
  - Batch DAO Queries:
    - Extended `FavoriteDao`, `MeditationHistoryDao`, and `UserSettingDao` with batch select, insert, and clear routines.
  - UI Enhancements in `SettingsScreen`:
    - Added "Cadangan & Privasi Data" Card with "100% Privat" security badge.
    - Real-time display of user data footprint: `X Kutipan Disimpan • Y Sesi Meditasi`.
    - 1-tap "Ekspor JSON" triggering system share sheet to Google Drive, WhatsApp, Telegram, or File Manager.
    - 1-tap "Pulihkan" opening SAF system file picker (`application/json`) with success/failure feedback alert banners.
  - Unit Tests:
    - Added `UserDataBackupTest` testing JSON serialization, deserialization, stats, and restore operations.
    - JVM tests pass 100%.

## [0.9.0] - 2026-09-27

### Added
- **Phase 8 Google Drive Content Distribution**:
  - `GoogleDriveUrlResolver`:
    - Robust extraction of Google Drive File IDs across multiple URL structures (`/file/d/...`, `/open?id=...`, `/uc?id=...`, raw ID).
    - Automatic direct download endpoint resolution (`https://drive.google.com/uc?export=download&id=...`).
    - Detection of Google Drive quota exceeded responses in localized HTML / plain text.
    - Detection of Google Drive virus scan warning pages on large files with `confirm` token extraction.
  - `DriveContentClient`:
    - Resilient network client with automated multi-hop redirect following and cookie management.
    - Exponential backoff retry loop (up to 3 attempts with 1s, 2s, 4s progressive delays).
    - Network availability checks (`NET_CAPABILITY_INTERNET`) via Android `ConnectivityManager`.
    - Distinct error classification: `NoInternetConnection`, `StorageQuotaExceeded`, `Timeout`, `FileNotFound`, and `HttpError`.
    - Progress reporting callback during streaming downloads.
  - `ContentUpdateManager` Integration:
    - Extended with `DriveContentClient` for cloud distribution.
    - Seamless fallback: if remote Google Drive quota is exceeded or device is offline, automatically falls back to bundled asset manifest and cached packages.
    - Configurable remote distribution endpoint persistence in `app_content_config` table.
  - UI Enhancements in `SettingsScreen`:
    - Display of "Kanal Distribusi" (`Google Drive (Auto/Local)` or `Google Drive Cloud`).
    - Clear user feedback banners for cloud updates and quota alerts.
  - Unit Tests:
    - Added `GoogleDriveUrlResolverTest` covering URL formats, direct links, quota alerts, and token extraction.
    - Added `ContentUpdateTest` exception classification tests.
    - All tests passing 100% on JVM.
    - Live verification on connected physical Xiaomi Redmi Note 8 Pro (`com.dearyoti.soulquote`).

## [0.8.0] - 2026-09-27

### Added
- **Phase 7 Dynamic Content Update System**:
  - `ContentManifest`:
    - Structured DTO with parser for `manifestVersion`, `contentVersion`, `minAppVersion`, `packageUrl`, `packageSizeBytes`, `packageChecksumSha256`, `changelog`, `quotesCount`, and `meditationsCount`.
    - Checksum normalization (`lowercase` and trimmed) preventing hash comparison false-negatives.
  - `ContentPackage`:
    - Robust JSON parser mapping incoming delta payloads to Room entities (`QuoteEntity`, `QuoteCategoryEntity`, `TemplateEntity`, `MeditationEntity`, `MeditationCategoryEntity`).
  - `ContentUpdateManager`:
    - Safe download/extraction to isolated cache directory `cacheDir/staging/`.
    - Cryptographic SHA-256 integrity verification against package manifest before processing.
    - Version guard (`contentVersion` comparator and `minAppVersion` compatibility check against `BuildConfig.VERSION_CODE`).
    - Atomic database transaction (`withTransaction`) with automatic rollback on error to protect local data integrity.
    - Automatic cleanup of temporary staging files upon completion or failure.
  - Presentation & UI in `SettingsScreen`:
    - Interactive "Pembaruan Konten" Card with live version pill badge (`v3` -> `v4`).
    - Real-time content statistics (`72 Kutipan • 4 Meditasi` -> `77 Kutipan • 4 Meditasi`).
    - Dismissable status and success alert banners.
    - Package preview showing changelog, file size in KB, and new quote count.
    - "Perbarui Sekarang" action button with progress feedback (`LinearProgressIndicator` and step descriptions).
    - "Periksa Pembaruan Konten" button with live circular loading indicator.
  - Seed Content Package:
    - Bundled `seed/content_manifest.json` and `seed/content_update_v4.json` containing 5 new quotes by Bunda Arsaningsih.
  - Verification & Testing:
    - Unit tests (`ContentUpdateTest`) covering manifest parsing, package entity transformation, SHA-256 file hashing, and version check logic.
    - Live physical device verification on Xiaomi Redmi Note 8 Pro (`com.dearyoti.soulquote`): verified update check, atomic merge from v3 to v4 (72 -> 77 quotes), and immediate appearance of newly added quotes in `ExploreScreen`.

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
