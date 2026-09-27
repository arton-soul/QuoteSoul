# SoulQuote — Master Plan

## 1. Project Overview
**SoulQuote** is an offline-first Android application designed for inspiration, daily quotes, meditation, self-reflection, background ambient audio, and social quote card creation (Quote Studio).

### Core Principles
- **Offline First**: Full functional availability without network access once initial data is local.
- **Data Segregation**: Clear isolation between developer-supplied content and user personal data.
- **Dynamic Content Updates**: Content packages distributed via Google Drive without requiring APK updates.
- **Minimal Dependencies & Stability**: Lightweight, maintainable, secure, and testable code.

---

## 2. Development Phases Roadmap

### Phase 0 — Project Foundation
- Baseline Android Jetpack Compose setup
- Clean Architecture directory layout (`core`, `data`, `domain`, `presentation`)
- Gradle Kotlin DSL configuration & wrapper setup
- Environment verification & Git initialization
- Complete `/docs` documentation suite

### Phase 1 — Local Database Architecture
- Room Database setup with strict separation between Developer Content and User Data
- Developer Content entities: `quotes`, `quote_categories`, `meditations`, `meditation_categories`, `audio_assets`, `backgrounds`, `templates`, `app_content_config`
- User Data entities: `favorites`, `meditation_history`, `user_settings`, `downloaded_audio`, `journal_entries`, `notification_settings`
- Seed data bundling via pre-populated assets
- Room DAOs, Repositories, and migration policies

### Phase 2 — Quote System
- Quote exploration: list by category, search by tag/keyword, random quote generator
- Daily Quote engine: date-based calculation, timezone-aware scheduling, fallback to random
- Favorite quotes toggle and persistence
- Quote details view with author and source attribution

### Phase 3 — Notification Engine
- Daily Quote notification scheduling using Android `AlarmManager` / `WorkManager`
- Custom notification sound channel and audio playback
- Meditation reminder scheduler with configurable days, time, and sound
- Android 13+ `POST_NOTIFICATIONS` runtime permission handling
- Exact alarm permission handling (`SCHEDULE_EXACT_ALARM`) with graceful fallback

### Phase 4 — Meditation Engine
- Meditation audio catalog categorized by intent (focus, sleep, anxiety, gratitude)
- Media player implementation using Android Media3 / ExoPlayer
- Offline playback support: background download manager, checksum validation, and local storage management
- Playback state tracking (play, pause, seek, completion progress, history logging)

### Phase 5 — Ambient & Background Audio System
- Ambient sound player supporting layered audio (rain, ocean, forest, fireplace, white noise)
- Looping playback, individual volume sliders, and mute controls
- Proper Audio Focus handling (pause on call, duck on notification)

### Phase 6 — Quote Studio
- Custom Compose canvas for creating aesthetic quote cards
- Customization: background selection (solid, gradient, developer images, user gallery)
- Typography controls: fonts, sizes, text alignments, line heights, colors, drop shadows
- Watermark toggle and branding overlay
- High-resolution bitmap rendering via Compose DrawScope / Android Bitmap
- Image export to Gallery and Android Sharesheet via `FileProvider`

### Phase 7 — Content Update System
- Remote manifest checking (`contentVersion`, `minAppVersion`, `checksum`, `databaseUrl`)
- Safe transaction flow: Download to staging -> Verify checksum -> Backup current -> Swap -> Verify -> Activate
- Rollback capability to restore previous content if corruption is detected
- Update status callbacks and non-intrusive UI progress

### Phase 8 — Google Drive Content Distribution
- Google Drive as file distribution storage for `manifest.json`, content packages (`.zip`), and audio bundles
- Direct public link / file ID resolver
- Network resilience: timeouts, exponential backoff retries, interruption recovery

### Phase 9 — User Data Backup & Privacy
- Independent export/import mechanism for User Data (favorites, journal, history, settings)
- Strict exclusion of user private data from developer content updates
- Zero unconsented telemetry or cloud tracking

### Phase 10 — Modern Features & Enhancements
- Meditation streaks & mindful achievements
- Morning reflection & Evening unwind prompts
- Daily mood check-in & reflective journaling
- Glanceable Android App Widgets (Daily Quote widget)
- Deep linking (`soulquote://quote/{id}`, `soulquote://meditation/{id}`)

### Phase 11 — Quality Assurance & Testing
- Unit tests for repository layers, daily quote rotation algorithms, and manifest parser
- Room Database integration and migration tests
- Notification scheduling verification across API levels 26–35
- Download resilience & checksum corruption tests
- Offline mode test verification

### Phase 12 — Release Preparation
- R8/ProGuard obfuscation & resource shrinking optimization
- Keystore signing configuration
- Android Play Store release bundle & privacy policy compliance
