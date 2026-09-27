# SoulQuote — Release & Deployment Guide

## 1. Build & Release Artifacts
SoulQuote uses Android App Bundle (`.aab`) for production distribution on the Google Play Store and APK (`.apk`) for testing.

### Commands
- Debug Build: `.\gradlew.bat assembleDebug`
- Release AAB: `.\gradlew.bat bundleRelease`
- Unit Test Run: `.\gradlew.bat test`
- Android Lint Check: `.\gradlew.bat lint`

---

## 2. Signing Configuration
Production signing credentials must never be committed to source control. They are read from `keystore.properties` in the project root:

```properties
storeFile=../soulquote_release.jks
storePassword=SECURE_STORE_PASSWORD
keyAlias=soulquote_key
keyPassword=SECURE_KEY_PASSWORD
```

---

## 3. R8 / ProGuard Optimization
- Minification (`isMinifyEnabled = true`) and resource shrinking (`isShrinkResources = true`) are enabled on release builds.
- Critical classes (Room entities, DAOs, and content DTOs) are preserved via `app/proguard-rules.pro`.

---

## 4. Play Store Compliance & Checklist
- [ ] Target SDK set to 35 (Android 15 compliance).
- [ ] Proper `foregroundServiceType="mediaPlayback"` declared if media session runs in foreground service.
- [ ] Data Safety Form: Declare zero tracking, zero personal data shared with 3rd parties.
- [ ] Privacy Policy hosted and up-to-date.
