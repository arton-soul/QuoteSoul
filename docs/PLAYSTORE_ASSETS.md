# 🎨 Asset Grafis Google Play Store — SoulQuote

Dokumen ini berisi panduan lengkap aset grafis dan materi publikasi untuk rilis aplikasi **SoulQuote** di Google Play Console.

---

## 💎 Filosofi Desain Logo Baru

Logo baru SoulQuote dirancang dengan konsep:
- **Kombinasi Elegan**: Menggabungkan **tanda petik kutipan kebijaksanaan (*quotation marks*)** yang menyatu harmonis dengan **bentuk jantung (*heart*)**.
- **Makna Simbolik**: 
  - *Heart* (Jantung): Melambangkan pusat jiwa (*soul*), ketenangan batin, cinta kasih (*metta*), dan kesehatan emosional.
  - *Quote* (Kutipan): Melambangkan kata-kata bijak harian yang menyentuh sanubari dan menginspirasi hidup.
- **Palet Warna**: Gradien *Warm Terracotta & Rose Gold* di atas latar belakang *Deep Obsidian Dark* (`#121413`), menghadirkan nuansa premium, tenang, modern, dan meditatif.

---

## 📁 Daftar Asset Siap Upload ke Google Play Console

Seluruh file telah disiapkan di folder: `D:\Android\Projectku\SoulQuote\playstore\`

| Nama File | Dimensi (px) | Format & Ukuran | Spesifikasi Play Store | Keterangan |
| :--- | :--- | :--- | :--- | :--- |
| **`app_icon_512.png`** | **512 x 512** | PNG (32-bit), ~240 KB | Wajib (Maks 1024 KB, PNG 32-bit) | Ikon aplikasi utama Play Store |
| **`feature_graphic_1024x500.png`** | **1024 x 500** | PNG (24-bit), ~746 KB | Wajib (Maks 15 MB, 1024x500) | Banner promosi fitur di Play Store |
| **`screenshot_1_home.png`** | **1080 x 2400** | PNG, ~232 KB | Wajib (Minimal 2 tangkapan layar) | Refleksi Harian & Pelacak Suasana Hati |
| **`screenshot_2_meditation.png`** | **1080 x 2400** | PNG, ~189 KB | Wajib | Katalog Panduan Suara Meditasi |
| **`screenshot_3_player.png`** | **1080 x 2400** | PNG, ~218 KB | Wajib | Pemutar Audio Lengkap & Pengaturan Kecepatan |
| **`screenshot_4_settings.png`** | **1080 x 2400** | PNG, ~351 KB | Rekomendasi | Pengaturan, Cadangan Data & Mode Offline |
| **`screenshot_5_studio.png`** | **1080 x 2400** | PNG, ~367 KB | Rekomendasi | Studio Pembuat Kartu Kutipan Estetik |

---

## 📱 In-App Launcher Icons (Telah Diterapkan ke Proyek)

Logo baru ini juga telah diterapkan secara langsung ke dalam resource aplikasi Android:
- **Adaptive Icon (API 26+)**:
  - Background: `app/src/main/res/drawable/ic_launcher_background.xml` (Deep Obsidian `#121413`)
  - Foreground: `app/src/main/res/drawable/ic_launcher_foreground.xml` (Vector Heart-Quote Motif)
- **Raster Mipmaps (Legacy & Custom Launchers)**:
  - `mipmap-mdpi` (48x48)
  - `mipmap-hdpi` (72x72)
  - `mipmap-xhdpi` (96x96)
  - `mipmap-xxhdpi` (144x144)
  - `mipmap-xxxhdpi` (192x192)
  - Tersedia varian kotak (`ic_launcher.png`) dan bundar (`ic_launcher_round.png`).

---

## 📝 Rekomendasi Deskripsi Toko (Store Listing Metadata)

### 1. Judul Aplikasi (App Title - Maks 30 Karakter)
```text
SoulQuote: Refleksi & Meditasi
```

### 2. Deskripsi Singkat (Short Description - Maks 80 Karakter)
```text
Kutipan bijak harian, meditasi terpandu, dan suara alam untuk ketenangan jiwa.
```

### 3. Deskripsi Lengkap (Full Description - Maks 4000 Karakter)
```text
SoulQuote adalah ruang hening pribadi Anda di tengah kesibukan dunia modern. Dirancang khusus untuk memulihkan energi batin, menumbuhkan kesadaran diri, dan merawat kesehatan emosional Anda melalui perpaduan kutipan kebijaksanaan dan audio meditasi mendalam.

✨ FITUR UNGGULAN SOULQUOTE:

1. Refleksi Batin & Mood Harian
Mulailah hari dengan kutipan bijak penuh makna dari tokoh filsafat (Stoicism, Zen, Spiritualitas). Catat refleksi batin dan suasana hati Anda setiap hari secara privat.

2. Panduan Meditasi Mendalam
Akses sesi meditasi audio terpandu bersama instruktur berpengalaman:
- Pembersihan rekaman emosi dan luka batin
- Penyelarasan kesadaran dan energi cinta kasih
- Relaksasi tubuh total untuk istirahat dan tidur lelap
- Pembersihan karakter malas dan penumbuhan semangat

3. Suara Alam Meditatif
Nikmati alunan suara alam relaksasi seperti gemericik hujan, ombak laut, gemerisik api unggun, dan denting mangkuk Tibet (Tibetan singing bowl) untuk fokus belajar, bekerja, atau meditasi hening.

4. 100% Fleksibel & Mode Offline
Audio favorit dapat diunduh untuk didengarkan kapan saja tanpa kuota internet atau saat berada dalam mode pesawat.

5. Studio Kutipan Estetik
Kreasikan kutipan favorit Anda dengan berbagai template kartu yang indah dan bagikan inspirasi ke media sosial dengan mudah.

6. Privasi Penuh Tanpa Iklan yang Mengganggu
Seluruh catatan refleksi dan data pribadi Anda tersimpan aman di perangkat lokal Anda.

Temukan kembali kedamaian dan keheningan jiwamu hari ini bersama SoulQuote.
```

---

## 🚀 Panduan Upload di Google Play Console

1. Buka [Google Play Console](https://play.google.com/console).
2. Pilih aplikasi **SoulQuote**.
3. Di bilah navigasi kiri, gulir ke **Pertumbuhan (Growth)** ➔ **Keberadaan di Store (Store presence)** ➔ **Listingan store utama (Main store listing)**.
4. Pada bagian **Aset Grafis (Graphics)**:
   - **Ikon Aplikasi**: Unggah `playstore/app_icon_512.png`.
   - **Grafik Fitur**: Unggah `playstore/feature_graphic_1024x500.png`.
   - **Tangkapan Layar Ponsel**: Unggah `screenshot_1_home.png` hingga `screenshot_5_studio.png`.
5. Klik **Simpan (Save)** di kanan bawah.
