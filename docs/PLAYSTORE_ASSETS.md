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

### 1. Metadata Bahasa Indonesia (Default)

#### A. Judul Aplikasi (App Title - Maks 30 Karakter)
```text
SoulQuote: Refleksi & Meditasi
```

#### B. Deskripsi Singkat (Short Description - Maks 80 Karakter)
```text
Kutipan bijak harian, meditasi terpandu, dan suara alam untuk ketenangan jiwa.
```

#### C. Deskripsi Lengkap (Full Description - Maks 4000 Karakter)
```text
SoulQuote adalah ruang hening pribadi Anda di tengah kesibukan dan kebisingan dunia modern. Dirancang khusus untuk memulihkan energi batin, menumbuhkan kesadaran diri (*mindfulness*), dan merawat kesehatan emosional Anda melalui perpaduan kutipan kebijaksanaan abadi dan audio meditasi mendalam.

✨ FITUR LENGKAP & KEUNGGULAN SOULQUOTE:

1. Refleksi Batin & Jurnal Mindful Multi-Catatan
Mulailah dan akhiri hari Anda dengan perenungan yang bermakna. Kini Anda dapat mencatat beberapa refleksi harian kapan saja tanpa batasan jumlah entri, lengkap dengan penanda waktu yang presisi, riwayat refleksi, serta opsi pengeditan yang fleksibel.

2. Pelacak Suasana Hati (Mood Tracker) yang Menenangkan
Ekspresikan suasana hatimu dengan 6 spektrum emosi (Damai, Bersyukur, Tenang, Bersemangat, Lelah, Gelisah) dalam antarmuka visual yang nyaman, seimbang, dan menenangkan mata.

3. Panduan Meditasi Mendalam Bersama Instruktur
Nikmati rangkaian sesi meditasi terpandu yang dirancang untuk membersihkan beban mental dan menata kembali ketenangan jiwa:
• Pembersihan rekaman emosi dan pelepasan luka batin
• Penyelarasan kesadaran diri dan pancaran energi cinta kasih
• Relaksasi tubuh total untuk melepaskan ketegangan fisik dan tidur lelap
• Pembersihan karakter malas dan pembangkitan fokus serta motivasi hidup

4. Alunan Suara Alam Meditatif (Soundscapes)
Hadirkan atmosfer damai di mana saja dengan alunan instrumen dan suara alam berkualitas tinggi: suara hujan menenangkan, deburan ombak laut, gemerisik api unggun malam, dan denting mangkuk Tibet (*Tibetan singing bowl*) untuk fokus belajar, bekerja, atau meditasi hening.

5. Runtutan Kesadaran (Mindful Streak) & 6 Lencana Pencapaian
Bangun kebiasaan hening harian yang konsisten. Pantau rekor hari latihan Anda (*streak*) dan raih 6 lencana apresiasi batin (Langkah Pertama, Fokus 3 Hari, Seminggu Damai, Kedamaian Batin, Zen Master, Penyelam Batin).

6. Widget Layar Utama (Daily Quote Widget)
Sematkan kutipan inspiratif langsung di layar utama (*Home Screen*) ponsel Anda. Dilengkapi tombol segarkan instan untuk berganti kutipan bijak kapan pun Anda membutuhkan percikan motivasi tanpa harus membuka aplikasi.

7. Studio Kutipan Estetik & Pembuat Kutipan Pribadi (Quote Studio)
Ubah kutipan favorit atau tulis kata-kata bijak karyamu sendiri menjadi karya seni visual yang anggun. Dilengkapi fitur lengkap:
• Tulis dan simpan kutipan pribadi langsung ke database privatmu
• Pengaturan redup/opasitas foto galeri pribadi untuk kenyamanan membaca
• Geser & posisikan letak kalimat secara bebas di atas kanvas
• Latar kotak teks khusus dengan aneka pilihan warna & opasitas
• Beragam rasio kanvas (1:1 untuk feed, 9:16 untuk story/status, 4:5), pilihan font elegan, dan bagikan inspirasi kebahagiaan seketika.

8. Mode 100% Fleksibel & Hemat Kuota (Offline-First)
Audio meditasi utama siap diputar seketika. Anda bebas mengalirkan (*streaming*) sesi lainnya atau mengunduhnya ke penyimpanan perangkat untuk dinikmati kapan saja tanpa koneksi internet atau dalam mode pesawat.

9. Cadangan Data Terenkripsi & Privasi Mutlak (100% Privat)
Privasi Anda adalah prioritas tertinggi kami. SoulQuote beroperasi tanpa pelacak (*zero telemetry*), tanpa analitik tersembunyi, dan tanpa iklan yang mengganggu. Seluruh catatan jurnal tersimpan privat di perangkat Anda, lengkap dengan fitur ekspor-impor cadangan JSON yang aman.

10. Dukungan Multi-Bahasa (Bilingual)
SoulQuote mendukung Bahasa Indonesia dan Bahasa Inggris secara penuh, yang dapat Anda alihkan dengan mudah dan instan melalui menu Pengaturan.

11. Lonceng Kesadaran & Pengingat Lembut (Mindful Bell & Reminders)
Pengingat harian yang menenangkan dengan denting lonceng kesadaran (*mindful bell*) dan alunan mangkuk bernyanyi Zen (*Tibetan singing bowl*) untuk mengajak Anda jeda sejenak dan bernapas di tengah hiruk-pikuk aktivitas.

---
⚠️ PENAFIAN & ATRIBUSI SUMBER (DISCLAIMER):
Aplikasi SoulQuote adalah karya independen yang dikembangkan untuk mempermudah latihan refleksi diri dan meditasi SOUL Reflection. Aplikasi ini TIDAK berafiliasi resmi, bermitra, disponsori, atau disahkan oleh Bunda Arsaningsih maupun organisasi/perusahaan SOUL. Seluruh kutipan dan materi panduan meditasi bersumber dan terinspirasi dari tayangan publik di Kanal YouTube Resmi Bunda Arsaningsih. Seluruh hak cipta materi orisinal sepenuhnya merupakan milik pencipta aslinya.
---

Temukan kembali keheningan, keseimbangan, dan kedamaian jiwamu hari ini bersama SoulQuote.
```

---

### 2. Metadata English (Secondary Language)

#### A. App Title (Max 30 Characters)
```text
SoulQuote: Mindfulness & Peace
```

#### B. Short Description (Max 80 Characters)
```text
Daily wisdom quotes, guided meditation, and nature sounds for inner peace.
```

#### C. Full Description (Max 4000 Characters)
```text
SoulQuote is your personal sanctuary of stillness amidst the noise of the modern world. Carefully crafted to restore inner harmony, nurture mindfulness, and support emotional well-being through timeless philosophical wisdom and immersive guided meditation.

✨ KEY FEATURES & HIGHLIGHTS OF SOULQUOTE:

1. Multi-Entry Mindful Journal & Daily Reflection
Begin and close your day with purposeful contemplation. Record multiple journal reflections per day with precise timestamps, full editing capabilities, and a reflective history log.

2. Soothing Daily Mood Tracker
Acknowledge your emotional state with 6 balanced emotional spectrums (Peaceful, Grateful, Calm, Energetic, Tired, Anxious) framed in a serene, distraction-free interface.

3. Deep Guided Meditation Sessions
Access audio meditation sessions led by experienced guides, designed to bring mental clarity and emotional release:
• Emotional cleansing and letting go of past burdens
• Heart coherence, self-compassion, and loving-kindness
• Deep bodily relaxation for stress relief and restorative sleep
• Overcoming lethargy and revitalizing mindful focus

4. Meditative Nature Soundscapes & Ambient Audio
Create a peaceful environment anywhere with high-fidelity ambient soundscapes: soothing rain, gentle ocean waves, fireside warmth, and resonance of Tibetan singing bowls for focused work, study, or quiet contemplation.

5. Mindful Streaks & 6 Milestone Badges
Cultivate a sustainable daily practice. Track consecutive mindfulness days and unlock 6 meaningful achievement badges along your inner journey.

6. Home Screen Daily Quote Widget
Keep daily wisdom within sight with our dedicated 4x2 Home Screen widget. Features an instant refresh button to cycle inspirational quotes directly from your launcher.

7. Aesthetic Quote Card Studio & Custom Wisdom Creator
Transform wisdom quotes or compose your own personal insights into breathtaking graphic cards:
• Write and save personal quotes directly to your secure private database
• Adjust custom photo opacity and dimming for optimal text clarity
• Freely drag and reposition quotes on the canvas with quick alignment shortcuts
• Customize dedicated text container background cards with vivid palettes and opacity controls
• Multi-format canvas ratios (1:1 square, 9:16 story, 4:5 portrait), elegant fonts, and instant sharing.

8. 100% Offline-First & Storage-Friendly
Core meditation audio is ready out-of-the-box. Stream additional sessions seamlessly or download them directly to your device for offline playback on airplanes or off-grid retreats.

9. Absolute Privacy & Zero Telemetry
Your inner thoughts belong exclusively to you. SoulQuote operates with zero tracking, no cloud telemetry, and no disruptive ads. All reflections remain strictly on your local device, complete with secure encrypted JSON backup and restore.

10. Bilingual Experience
Easily toggle between Indonesian and English anytime with instant UI reactivity in Settings.

11. Mindful Bells & Gentle Reminders
Gentle daily notifications accompanied by authentic mindful bells and resonant Tibetan singing bowls to invite mindful pauses and deep breaths throughout your day.

---
⚠️ DISCLAIMER & CONTENT ATTRIBUTION:
SoulQuote is an independent application created to facilitate personal mindfulness and SOUL Reflection meditation practice. It is NOT officially affiliated with, endorsed by, sponsored by, or partnered with Bunda Arsaningsih or the SOUL organization. All quotes and meditation references are inspired by and derived from public videos on Bunda Arsaningsih's official YouTube Channel. All original copyrights remain the property of their respective creator.
---

Rediscover stillness, balance, and tranquility in your daily life with SoulQuote.
```

---

## 📋 Catatan Rilis / Keterangan Versi (What's New in this Release)

Gunakan teks di bawah ini untuk mengisi bagian **Keterangan Rilis / Release Notes** di Google Play Console pada setiap pembaruan:

### 🌟 Versi 0.3.0 (Version Code: 3) — Terkini (Current Release)

#### 🇮🇩 Bahasa Indonesia (`<id-ID>`):
```text
<id-ID>
Pembaruan SoulQuote v0.3.0 menyempurnakan keandalan dan pengalaman meditasi Anda:
• Sinkronisasi Privasi & Cadangan: Statistik data kini terhubung langsung dengan jurnal refleksi, favorit, dan riwayat secara waktu-nyata (real-time).
• Uji Suara Lonceng & Mangkuk Zen: Uji coba nada lonceng kesadaran (mindful bell) dan mangkuk bernyanyi (zen singing bowl) kini dapat didengar langsung dari aplikasi dengan suara jernih.
• Pengingat & Notifikasi Lebih Responsif: Pengaturan jadwal pengingat kutipan harian dan meditasi malam kini dilengkapi notifikasi konfirmasi bilingual yang interaktif.
• Peningkatan Stabilitas: Optimasi alur perizinan notifikasi dan performa di berbagai perangkat Android.
</id-ID>
```

#### 🇺🇸 English (`<en-US>`):
```text
<en-US>
SoulQuote v0.3.0 brings refined reliability and audio responsiveness to your mindful practice:
• Real-time Backup & Privacy Sync: Live synchronization between local backup stats, mindful journal reflections, favorites, and meditation history.
• Direct Bell & Zen Singing Bowl Audio Test: Instantly listen to mindful bell and Tibetan singing bowl tones directly in-app with crystal clarity.
• Interactive Reminder Feedback: Interactive bilingual feedback when toggling and scheduling daily wisdom and evening meditation reminders.
• Performance & Stability: Enhanced Android 13+ notification permission flow and general performance optimizations.
</en-US>
```

---

### 📦 Versi 0.2.0 (Version Code: 2) — Rilis Pembaruan

#### 🇮🇩 Bahasa Indonesia (`<id-ID>`):
```text
<id-ID>
Versi 0.2.0 membawa pembaruan menyeluruh untuk meningkatkan kenyamanan, estetika, dan keheningan jiwa Anda:
• Dukungan Multi-Bahasa: Kini mendukung Bahasa Indonesia dan Bahasa Inggris secara penuh, dapat diubah dengan mudah di menu Pengaturan.
• Jurnal Mindful Multi-Catatan: Catat refleksi harian kapan saja tanpa batasan jumlah entri, lengkap dengan waktu pencatatan, daftar catatan hari ini, fitur edit, dan riwayat.
• Penyempurnaan Tampilan Mood: Pilihan suasana hati ditata ulang dalam grid yang rapi, seimbang, dan nyaman dibaca tanpa teks terpotong.
• Ikon Aplikasi & Desain Baru: Tampilan logo baru yang anggun memadukan simbol kutipan bijak dengan bentuk hati (heart-quote).
• Optimalisasi Audio Meditasi: Peningkatan stabilitas streaming suara dan manajemen unduhan offline yang lebih hemat ruang penyimpanan.
</id-ID>
```

#### 🇺🇸 English (`<en-US>`):
```text
<en-US>
Version 0.2.0 introduces major enhancements to enrich your mindfulness and reflection experience:
• Multi-Language Support: Full bilingual experience in Indonesian and English, easily toggleable via Settings.
• Multi-Entry Mindful Journal: Record multiple reflections per day with precise timestamps, today's entry list, editing, and history management.
• Redesigned Mood Selector: Balanced, comfortable mood grid layout ensuring clean text and smooth interaction.
• Elegant New Branding: Brand-new heart-quote app icon and refined visual identity.
• Meditation Audio Enhancements: Smoother streaming performance and optimized offline download management.
</en-US>
```

---

### 📦 Versi 0.1.0 (Version Code: 1) — Rilis Perdana (Initial Release)

#### 🇮🇩 Bahasa Indonesia (`<id-ID>`):
```text
<id-ID>
Selamat datang di SoulQuote — Ruang hening pribadi untuk ketenangan jiwa dan refleksi diri:
• Ratusan kutipan bijak harian dari filosofi Stoicism, Zen, dan Timur.
• Sesi meditasi terpandu dan alunan suara alam relaksasi (soundscapes).
• Pelacak suasana hati dan jurnal refleksi harian.
• Studio kartu kutipan estetik untuk berbagi inspirasi ke media sosial.
• 100% Offline-First, hemat kuota, dan privasi data terjamin aman di perangkat Anda.
</id-ID>
```

#### 🇺🇸 English (`<en-US>`):
```text
<en-US>
Welcome to SoulQuote — Your personal sanctuary for mindfulness and inner peace:
• Curated collection of daily wisdom quotes across Stoicism, Zen, and Eastern traditions.
• Guided meditation sessions and immersive ambient nature soundscapes.
• Daily reflection journal and mood tracking.
• Aesthetic Quote Card Studio for crafting and sharing inspirational cards.
• 100% Offline-first architecture, zero telemetry, and privacy-focused local storage.
</en-US>
```

### 📦 Versi 0.2.0 (Version Code: 2) — Multi-Bahasa & Visual Baru
#### 🇮🇩 Bahasa Indonesia (`<id-ID>`):
```text
<id-ID>
Pembaruan SoulQuote v0.2.0:
• Dukungan Multi-Bahasa (Bahasa Indonesia & English) langsung dari Pengaturan.
• Peningkatan Jurnal Refleksi: kini mendukung multi-catatan harian dengan timestamp presisi.
• Antarmuka Mood Tracker lebih seimbang dan nyaman di mata.
• Logo dan ikon aplikasi baru yang anggun serta optimalisasi pemutar audio meditasi.
</id-ID>
```
#### 🇺🇸 English (`<en-US>`):
```text
<en-US>
SoulQuote v0.2.0 Update:
• Full Bilingual Support (Indonesian & English) toggleable in Settings.
• Multi-entry daily reflection journal with exact timestamps and editing.
• Refreshed, balanced mood tracker interface.
• New elegant brand identity and optimized audio meditation playback.
</en-US>
```

### 📦 Versi 0.3.0 (Version Code: 3) — Uji Lonceng, Sinkronisasi Data & Konten v8
#### 🇮🇩 Bahasa Indonesia (`<id-ID>`):
```text
<id-ID>
Pembaruan SoulQuote v0.3.0:
• Sinkronisasi reaktif data favorit, riwayat, dan jurnal refleksi pada menu Cadangan & Privasi.
• Uji Lonceng Kesadaran & Mangkuk Zen langsung bersuara di dalam aplikasi.
• Pembaruan Konten v8: 60 kutipan baru tentang Misteri Doa, Kepasrahan & Hukum Semesta oleh Bunda Arsaningsih & dr. Rastho Mahotama.
• Notifikasi harian lebih interaktif dan izin pengingat yang mulus.
</id-ID>
```
#### 🇺🇸 English (`<en-US>`):
```text
<en-US>
SoulQuote v0.3.0 Update:
• Reactive synchronization for favorites, history, and journal stats in Backup & Privacy.
• Instant in-app audio preview for Mindful Bell and Tibetan Zen Singing Bowl.
• Content Update v8: 60 new wisdom quotes on the Mystery of Prayer, Surrender, and Universal Laws.
• Enhanced daily reminder notifications and smooth permission handling.
</en-US>
```

### 📦 Versi 0.4.0 (Version Code: 4) — Quote Studio Supercharge & Kutipan Pribadi
#### 🇮🇩 Bahasa Indonesia (`<id-ID>`):
```text
<id-ID>
Pembaruan SoulQuote v0.4.0:
• Pembuat Kutipan Pribadi: Tulis dan simpan kata-kata bijak karyamu sendiri ke database privatmu.
• Latar Khusus Kutipan: Pilihan aneka warna kotak latar teks dan opasitas untuk kontras yang sempurna.
• Geser & Atur Posisi: Pindahkan letak kutipan secara bebas di atas kanvas dengan gesture sentuh atau tombol cepat.
• Pengatur Redup Foto Galeri: Atur opasitas foto latar dari galeri agar teks selalu mudah dibaca.
• Pencadangan otomatis untuk seluruh kutipan pribadimu.
</id-ID>
```
#### 🇺🇸 English (`<en-US>`):
```text
<en-US>
SoulQuote v0.4.0 Update:
• Custom Quote Creator: Compose and save your own wisdom quotes to your private database.
• Text Box Backgrounds: Choose container colors and adjust opacity for impeccable readability.
• Freeform Repositioning: Move and align quote text on canvas via touch gestures or quick presets.
• Gallery Photo Dimmer: Adjust background photo brightness/opacity for stunning visual contrast.
• Full encrypted JSON backup support for your personal quotes collection.
</en-US>
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
