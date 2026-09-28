# SoulQuote — Release & Deployment Guide

Dokumen ini merupakan panduan komprehensif untuk proses build, penandatanganan (*signing*), optimasi R8/ProGuard, serta publikasi aplikasi **SoulQuote** (`com.dearyoti.soulquote`) ke Google Play Store, beserta pengelolaan pembaruan konten dinamis via GitHub.

---

## 1. Spesifikasi Rilis Aplikasi

- **Application ID**: `com.dearyoti.soulquote`
- **Version Code**: `3`
- **Version Name**: `0.3.0`
- **Min SDK**: `26` (Android 8.0 Oreo)
- **Target SDK**: `36` (Android 16 / Android 15+ Google Play Compliant)
- **Compile SDK**: `36`
- **Java / Kotlin Toolchain**: JDK 17
- **Format Distribusi Google Play**: Android App Bundle (`.aab`)
- **Format Pengujian Lokal**: Android Package (`.apk`)

---

## 2. Lokasi Berkas Rilis (Output Artifacts)

Setelah proses kompilasi rilis berhasil dieksekusi:

| Tipe Berkas | Lokasi Berkas | Ukuran | Kegunaan |
| :--- | :--- | :--- | :--- |
| **Release AAB** | [`app/build/outputs/bundle/release/app-release.aab`](file:///d:/Android/Projectku/SoulQuote/app/build/outputs/bundle/release/app-release.aab) | **9.08 MB** (9,518,591 bytes) | **Upload ke Google Play Console (Internal Testing)** |
| **Release APK** | [`app/build/outputs/apk/release/app-release.apk`](file:///d:/Android/Projectku/SoulQuote/app/build/outputs/apk/release/app-release.apk) | **6.25 MB** (6,555,683 bytes) | Pengujian langsung di perangkat fisik |

---

## 3. Catatan Rilis Play Store (Release Notes)

Teks siap salin untuk kolom **Keterangan Rilis (Release Notes)** di Google Play Console:

### Versi 0.3.0 (Version Code: 3) — Terkini (Current Release)
```text
<id-ID>
Pembaruan SoulQuote v0.3.0 menyempurnakan keandalan dan pengalaman meditasi Anda:
• Sinkronisasi Privasi & Cadangan: Statistik data kini terhubung langsung dengan jurnal refleksi, favorit, dan riwayat secara waktu-nyata (real-time).
• Uji Suara Lonceng & Mangkuk Zen: Uji coba nada lonceng kesadaran (mindful bell) dan mangkuk bernyanyi (zen singing bowl) kini dapat didengar langsung dari aplikasi dengan suara jernih.
• Pengingat & Notifikasi Lebih Responsif: Pengaturan jadwal pengingat kutipan harian dan meditasi malam kini dilengkapi notifikasi konfirmasi bilingual yang interaktif.
• Peningkatan Stabilitas: Optimasi alur perizinan notifikasi dan performa di berbagai perangkat Android.
</id-ID>

<en-US>
SoulQuote v0.3.0 brings refined reliability and audio responsiveness to your mindful practice:
• Real-time Backup & Privacy Sync: Live synchronization between local backup stats, mindful journal reflections, favorites, and meditation history.
• Direct Bell & Zen Singing Bowl Audio Test: Instantly listen to mindful bell and Tibetan singing bowl tones directly in-app with crystal clarity.
• Interactive Reminder Feedback: Interactive bilingual feedback when toggling and scheduling daily wisdom and evening meditation reminders.
• Performance & Stability: Enhanced Android 13+ notification permission flow and general performance optimizations.
</en-US>
```

### Versi 0.2.0 (Version Code: 2) — Rilis Pembaruan
```text
<id-ID>
Versi 0.2.0 membawa pembaruan menyeluruh untuk meningkatkan kenyamanan, estetika, dan keheningan jiwa Anda:
• Dukungan Multi-Bahasa: Kini mendukung Bahasa Indonesia dan Bahasa Inggris secara penuh, dapat diubah dengan mudah di menu Pengaturan.
• Jurnal Mindful Multi-Catatan: Catat refleksi harian kapan saja tanpa batasan jumlah entri, lengkap dengan waktu pencatatan, daftar catatan hari ini, fitur edit, dan riwayat.
• Penyempurnaan Tampilan Mood: Pilihan suasana hati ditata ulang dalam grid yang rapi, seimbang, dan nyaman dibaca tanpa teks terpotong.
• Ikon Aplikasi & Desain Baru: Tampilan logo baru yang anggun memadukan simbol kutipan bijak dengan bentuk hati (heart-quote).
• Optimalisasi Audio Meditasi: Peningkatan stabilitas streaming suara dan manajemen unduhan offline yang lebih hemat ruang penyimpanan.
</id-ID>

<en-US>
Version 0.2.0 introduces major enhancements to enrich your mindfulness and reflection experience:
• Multi-Language Support: Full bilingual experience in Indonesian and English, easily toggleable via Settings.
• Multi-Entry Mindful Journal: Record multiple reflections per day with precise timestamps, today's entry list, editing, and history management.
• Redesigned Mood Selector: Balanced, comfortable mood grid layout ensuring clean text and smooth interaction.
• Elegant New Branding: Brand-new heart-quote app icon and refined visual identity.
• Meditation Audio Enhancements: Smoother streaming performance and optimized offline download management.
</en-US>
```

### Versi 0.1.0 (Version Code: 1) — Rilis Perdana
```text
<id-ID>
Selamat datang di SoulQuote — Ruang hening pribadi untuk ketenangan jiwa dan refleksi diri:
• Ratusan kutipan bijak harian dari filosofi Stoicism, Zen, dan Timur.
• Sesi meditasi terpandu dan alunan suara alam relaksasi (soundscapes).
• Pelacak suasana hati dan jurnal refleksi harian.
• Studio kartu kutipan estetik untuk berbagi inspirasi ke media sosial.
• 100% Offline-First, hemat kuota, dan privasi data terjamin aman di perangkat Anda.
</id-ID>

<en-US>
Welcome to SoulQuote — Your personal sanctuary for mindfulness and inner peace:
• Curated collection of daily wisdom quotes across Stoicism, Zen, and Eastern traditions.
• Guided meditation sessions and immersive ambient nature soundscapes.
• Daily reflection journal and mood tracking.
• Aesthetic Quote Card Studio for crafting and sharing inspirational cards.
• 100% Offline-first architecture, zero telemetry, and privacy-focused local storage.
</en-US>
```

---

## 4. Konfigurasi Penandatanganan (Signing Keystore)

Aplikasi ditandatangani menggunakan sertifikat resmi PKCS12 yang dikonfigurasikan di [`app/build.gradle.kts`](file:///d:/Android/Projectku/SoulQuote/app/build.gradle.kts) melalui berkas `keystore.properties`:

- **Path Konfigurasi**: `D:\Android\Keystore\soul\keystore.properties`
- **Path Keystore**: `D:\Android\Keystore\soul\soul`
- **Tipe Keystore**: `PKCS12`
- **Key Alias**: `soul`
- **Algoritma**: 2048-bit RSA key (`SHA256withRSA`)
- **Masa Berlaku Sertifikat**: Hingga 28 Januari 3026 (1000 tahun)
- **Kepemilikan Sertifikat**:
  - `Owner / Issuer`: CN=Made Sudiartono, OU=Android, O=Soul, L=Denpasar, ST=Bali, C=ID
  - `SHA1 Fingerprint`: `D2:B0:FD:77:CB:29:7B:9A:D9:34:F1:40:EE:93:A6:C4:B1:6C:72:07`
  - `SHA256 Fingerprint`: `DC:A9:1F:F4:30:46:BC:E7:DE:13:4F:D4:3B:8C:54:86:1B:7B:18:CF:10:09:DD:9F:26:E2:CE:28:65:77:A0:A8`

> [!NOTE]
> Berkas `keystore.properties` dan file keystore dilindungi dan secara otomatis diabaikan oleh [`.gitignore`](file:///d:/Android/Projectku/SoulQuote/.gitignore) agar kredensial pribadi tidak pernah terunggah ke repositori publik.

---

## 5. Optimasi R8 & Resource Shrinking

Konfigurasi build tipe `release` mengaktifkan **Code Shrinking (Minification)** dan **Resource Shrinking** penuh:
- Ukuran APK menyusut dari **25.5 MB (debug)** menjadi **6.15 MB (release)** — reduksi ukuran sebesar **~76%**.
- Aturan penjagaan (*keep rules*) didefinisikan secara presisi di [`app/proguard-rules.pro`](file:///d:/Android/Projectku/SoulQuote/app/proguard-rules.pro):
  - Menjaga keutuhan Room Database, Entities, dan DAOs.
  - Menjaga DTO model serialisasi JSON (`ContentManifest`, `ContentPackage`, `UserDataBackupModel`).
  - Menjaga komponen Android: `DailyQuoteWidgetProvider`, `DailyQuoteNotificationReceiver`, `MeditationReminderReceiver`, dan `BootReceiver`.
  - Menjaga Media3 ExoPlayer classes untuk pemutaran audio guided meditation dan ambient soundscapes.

---

## 6. Distribusi Konten Dinamis via GitHub (OTA Update)

SoulQuote terintegrasi dengan repositori GitHub:
`https://github.com/arton-soul/QuoteSoul.git`

Endpoint manifes default yang ditanam di dalam aplikasi:
`https://raw.githubusercontent.com/arton-soul/QuoteSoul/main/distribution/content_manifest.json`

### Langkah Memperbarui Konten Tanpa Rilis APK Baru:
1. Siapkan paket pembaruan konten baru (misal `distribution/content_update_v5.json`).
2. Dapatkan ukuran bytes dan hash SHA-256 berkas tersebut di PowerShell:
   ```powershell
   (Get-Item distribution\content_update_v5.json).Length
   (Get-FileHash distribution\content_update_v5.json -Algorithm SHA256).Hash.ToLower()
   ```
3. Perbarui `distribution/content_manifest.json`:
   - Naikkan `contentVersion` (misal dari `4` menjadi `5`).
   - Perbarui `packageUrl` menuju URL raw GitHub.
   - Masukkan `packageSizeBytes` dan `packageChecksumSha256`.
4. Lakukan commit dan push ke GitHub:
   ```bash
   git add distribution/
   git commit -m "feat: publish content update v5"
   git push origin main
   ```
5. Pengguna di Google Play Store akan secara otomatis mendeteksi pembaruan dan mengunduhnya ke basis data lokal perangkat mereka.

---

## 7. Daftar Periksa Google Play Console (Play Store Checklist)

Sebelum mengunggah `app-release.aab` ke Google Play Console, pastikan hal-hal berikut telah dipenuhi:

- [x] **Android App Bundle (.aab)** telah ditandatangani dengan keystore resmi.
- [x] **Target SDK 36** memenuhi kewajiban kebijakan Google Play (minimal Android 14/15).
- [x] **Data Safety Form**:
  - SoulQuote adalah aplikasi **100% Offline-First**.
  - **Zero Telemetry / Zero Cloud Tracking**: Tidak ada data pribadi pengguna, entri jurnal batin, atau riwayat meditasi yang dikumpulkan atau dibagikan ke server pihak ketiga.
  - Data hanya disimpan di SQLite lokal pengguna (`soulquote_user.db`).
- [x] **Izin Sensitif**:
  - `POST_NOTIFICATIONS`: Digunakan secara transparan untuk pengingat harian kutipan dan meditasi.
  - `SCHEDULE_EXACT_ALARM`: Menggunakan pengecekan izin dan graceful fallback ke inexact alarms.
  - `WRITE_EXTERNAL_STORAGE`: Terbatas pada Android $\le$ 9 (`maxSdkVersion="28"`) untuk ekspor kartu gambar, sedangkan Android 10+ menggunakan `MediaStore` Scoped Storage tanpa izin khusus.
- [x] **Kebijakan Privasi (Privacy Policy)**:
  - Telah dipublikasikan secara resmi di Blogger: [`https://dearyoti.blogspot.com/p/privacy-policy-for-soulquote.html`](https://dearyoti.blogspot.com/p/privacy-policy-for-soulquote.html)
  - Tersedia tautan langsung di dalam aplikasi (Menu Pengaturan &gt; About SoulQuote &gt; Kebijakan Privasi).

---

## 8. Perintah Build Cepat (Cheatsheet)

```powershell
# Menjalankan seluruh Unit Test (13 test suites)
.\gradlew.bat testDebugUnitTest

# Membangun Release APK yang sudah ditandatangani
.\gradlew.bat assembleRelease

# Membangun Release AAB siap upload Google Play Console
.\gradlew.bat bundleRelease

# Verifikasi tanda tangan APK via apksigner
& "C:\Users\Asus\AppData\Local\Android\Sdk\build-tools\36.0.0\apksigner.bat" verify --verbose "app\build\outputs\apk\release\app-release.apk"
```
