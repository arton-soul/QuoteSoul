# SoulQuote — Architecture Documentation

## 1. Architectural Overview
SoulQuote is built on **Clean Architecture** combined with **MVVM (Model-View-ViewModel)** and **Unidirectional Data Flow (UDF)**. This guarantees strict separation of concerns, testability, and an offline-first foundation.

```mermaid
flowchart TD
    subgraph Presentation ["Presentation Layer (Jetpack Compose)"]
        UI["Compose Screens & Components"]
        VM["ViewModels (StateFlow / UDF)"]
        UI -->|User Events| VM
        VM -->|UI State (StateFlow)| UI
    end

    subgraph Domain ["Domain Layer (Pure Kotlin)"]
        UC["Use Cases / Interactors"]
        DM["Domain Models"]
        RI["Repository Interfaces"]
        VM --> UC
        UC --> RI
    end

    subgraph Data ["Data Layer"]
        RepoImpl["Repository Implementations"]
        LDS["Local Data Source (Room & Disk Storage)"]
        RDS["Remote Data Source (Google Drive Distribution)"]
        RI -.->|Implements| RepoImpl
        RepoImpl --> LDS
        RepoImpl --> RDS
    end

    subgraph Storage ["Persistent Storage"]
        RoomDB["Room SQLite DB (Content & User Data)"]
        FileStore["Audio & Image File Storage"]
        LDS --> RoomDB
        LDS --> FileStore
    end
```

---

## 2. Layer Breakdown

### 2.1. Presentation Layer (`com.soulquote.app.presentation`)
- **Technology**: Jetpack Compose, Material 3, Navigation Compose.
- **Responsibilities**:
  - Renders user interfaces declaratively based on immutable UI State.
  - Passes user intents to ViewModels.
  - Never accesses DAOs or repositories directly.
  - Uses `collectAsStateWithLifecycle()` to prevent resource waste when backgrounded.

### 2.2. Domain Layer (`com.soulquote.app.domain`)
- **Technology**: Pure Kotlin (no Android UI or database imports).
- **Responsibilities**:
  - Encapsulates business logic in reusable Use Cases (e.g., `GetDailyQuoteUseCase`, `DownloadMeditationAudioUseCase`).
  - Defines repository interfaces.
  - Defines pure domain entities free from Room annotations.

### 2.3. Data Layer (`com.soulquote.app.data`)
- **Technology**: Room DB, Kotlin Coroutines & Flow, WorkManager, OkHttp / Android DownloadManager.
- **Responsibilities**:
  - Implements repository interfaces.
  - Coordinates between Room local caching and remote update sources.
  - Mappers convert between database entities, DTOs, and domain models.
  - Manages atomic file operations on internal storage for audio files and templates.

### 2.4. Core Layer (`com.soulquote.app.core`)
- **Technology**: Theme, Utilities, Common extensions, Constants.
- **Responsibilities**:
  - Theme styling (Colors, Typography, Shapes).
  - Common result wrappers (`Resource<T>`).
  - Audio and Notification helpers.

---

## 3. Package Structure
```text
com.soulquote.app
├── SoulQuoteApp.kt
├── core/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   └── util/
│       ├── Resource.kt
│       └── Constants.kt
├── data/
│   ├── local/
│   │   ├── dao/
│   │   ├── entity/
│   │   └── SoulQuoteDatabase.kt
│   ├── remote/
│   │   └── GoogleDriveContentClient.kt
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
└── presentation/
    ├── MainActivity.kt
    ├── navigation/
    ├── home/
    ├── quotes/
    ├── meditation/
    ├── studio/
    └── settings/
```

---

## 4. Offline-First Principles
1. **Single Source of Truth (SSOT)**: The local Room database and device storage are the only sources consumed by the UI.
2. **Network Decoupling**: If network requests fail (Google Drive content checks or audio downloads), the UI displays cached local data with graceful status messages.
3. **Optimistic Updates**: Local modifications (e.g., favoriting a quote, adjusting settings) take effect instantly on disk.
