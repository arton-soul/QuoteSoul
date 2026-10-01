package com.soulquote.app.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    // Navigation
    val navHome: String
    val navExplore: String
    val navMeditation: String
    val navStudio: String
    val navSettings: String
    val navFavorites: String

    // Home
    val greetingMorning: String
    val greetingAfternoon: String
    val greetingEvening: String
    val greetingNight: String
    val todaysReflection: String
    val innerReflectionMood: String
    val writeReflection: String
    val editReflection: String
    val daysUnit: String
    val themesOfWisdom: String
    val seeAll: String
    val defaultJournalPrompt: String

    // Settings
    val settingsTitle: String
    val settingsSubtitle: String
    val sectionLanguage: String
    val languageSubtitle: String
    val indonesianOption: String
    val englishOption: String
    val languageChangedToast: String

    val sectionNotifications: String
    val dailyQuoteTitle: String
    val scheduledAt: String
    val changeTimeButton: String
    val meditationReminderTitle: String
    val gentleVibrationTitle: String
    val gentleVibrationDesc: String
    val testNotificationsTitle: String
    val testBellButton: String
    val testBowlButton: String
    val dailyQuoteEnabledToast: String
    val dailyQuoteDisabledToast: String
    val meditationReminderEnabledToast: String
    val meditationReminderDisabledToast: String
    val timeUpdatedToast: String
    val vibrationEnabledToast: String
    val vibrationDisabledToast: String
    val testBellTriggeredToast: String
    val testBowlTriggeredToast: String
    val notificationPermissionRequiredToast: String

    val sectionContentUpdates: String
    val contentVersionBadge: String
    val contentStatsLabel: String
    val quotesUnit: String
    val meditationsUnit: String
    val storageStrategyLabel: String
    val storageStrategyValue: String
    val distributionChannelLabel: String
    val checkUpdatesButton: String
    val checkingUpdatesLabel: String
    val updateNowButton: String

    val sectionDataBackup: String
    val privacyBadge: String
    val dataBackupDesc: String
    val personalStatsLabel: String
    val favoritesUnit: String
    val historyUnit: String
    val journalUnit: String
    val exportJsonButton: String
    val restoreButton: String

    val sectionAbout: String
    val aboutVersion: String
    val aboutDescription: String
    val privacyPolicyButton: String
    val disclaimerButton: String
    val disclaimerTitle: String
    val disclaimerBody: String
    val disclaimerVisitYoutube: String
    val rateAppButton: String
    val ratingPromptTitle: String
    val ratingPromptBody: String
    val ratingPromptRateButton: String
    val ratingPromptLaterButton: String
    val ratingNotificationTitle: String
    val ratingNotificationMessage: String
    val splashTagline: String
    val splashMindfulBreath: String

    // Meditation
    val meditationTitle: String
    val meditationSubtitle: String
    val tabVoiceGuide: String
    val tabNatureSounds: String
    val searchMeditationPlaceholder: String
    val filterAll: String
    val offlineBadge: String
    val playLabel: String
    val pauseLabel: String
    val minutesUnit: String
    val audioOfflineSaved: String
    val audioOfflineDeleted: String
    val audioDownloading: String
    val audioDownloadFailed: String

    // Studio
    val studioTitle: String
    val studioSubtitle: String
    val saveImageButton: String
    val shareButton: String
    val chooseThemeTitle: String
    val imageSavedSuccess: String

    // Extra Home Strings
    val guidedMeditationTitle: String
    val guidedMeditationDesc: String
    val ambientSoundscapesTitle: String
    val ambientSoundscapesDesc: String
    val loadingDailyQuote: String
    val openJournalButton: String
    val savedBadge: String
    val shareQuoteTitle: String

    // Extra Meditation Strings
    val emptyMeditationList: String
    val instructorPrefix: String
    val availableOfflineDesc: String
    val downloadOfflineDesc: String
    val chapterDiscourse: String
    val chapterMeditation: String
    val watchOnYouTube: String
    val watchOnYouTubeAtMeditation: String
    val meditationPhaseDiscourse: String
    val meditationPhasePractice: String

    // Extra Settings Strings
    val closeButton: String
    val updatePackageTitle: String
    val newQuotesCount: String
    val newMeditationsCount: String
    val applyingUpdateLabel: String
    val shareBackupTitle: String

    // Journal Strings
    val journalDialogTitle: String
    val tabTodayReflection: String
    val tabHistoryTitle: String
    val howIsYourMoodTitle: String
    val mindfulPromptHeader: String
    val changePromptButton: String
    val journalPlaceholder: String
    val savedTodayHeader: String
    val saveReflectionButton: String
    val updateReflectionButton: String
    val cancelEditButton: String
    val editingReflectionBanner: String
    val emptyHistoryMessage: String
    val deleteEntryButton: String
    val editEntryButton: String

    // Moods
    val moodPeaceful: String
    val moodGrateful: String
    val moodCalm: String
    val moodEnergetic: String
    val moodTired: String
    val moodAnxious: String

    companion object {
        val current: AppStrings
            @Composable
            @ReadOnlyComposable
            get() = LocalAppStrings.current
    }
}

object IndonesianStrings : AppStrings {
    // Navigation
    override val navHome = "Beranda"
    override val navExplore = "Jelajah"
    override val navMeditation = "Meditasi"
    override val navStudio = "Studio"
    override val navSettings = "Pengaturan"
    override val navFavorites = "Favorit"

    // Home
    override val greetingMorning = "Selamat Pagi"
    override val greetingAfternoon = "Selamat Siang"
    override val greetingEvening = "Selamat Sore"
    override val greetingNight = "Selamat Malam"
    override val todaysReflection = "Refleksi Hari Ini"
    override val innerReflectionMood = "Refleksi Batin & Mood"
    override val writeReflection = "Tulis Refleksi Hari Ini"
    override val editReflection = "Ubah Refleksi"
    override val daysUnit = "Hari"
    override val themesOfWisdom = "Tema Kebijaksanaan"
    override val seeAll = "Lihat Semua"
    override val defaultJournalPrompt = "Apa yang membuat jiwamu merasa tenang hari ini?"

    // Settings
    override val settingsTitle = "Pengaturan"
    override val settingsSubtitle = "Kustomisasi & Preferensi"
    override val sectionLanguage = "Bahasa (Language)"
    override val languageSubtitle = "Pilih bahasa tampilan antarmuka aplikasi"
    override val indonesianOption = "Bahasa Indonesia (Default)"
    override val englishOption = "English"
    override val languageChangedToast = "Bahasa berhasil diubah ke Bahasa Indonesia"

    override val sectionNotifications = "Pengingat & Notifikasi"
    override val dailyQuoteTitle = "Kutipan Harian"
    override val scheduledAt = "Dijadwalkan pukul %s"
    override val changeTimeButton = "Ubah Waktu (%02d:%02d)"
    override val meditationReminderTitle = "Pengingat Meditasi"
    override val gentleVibrationTitle = "Getaran Lembut"
    override val gentleVibrationDesc = "Getaran haptik bersama notifikasi"
    override val testNotificationsTitle = "Uji Suara & Notifikasi"
    override val testBellButton = "Uji Lonceng"
    override val testBowlButton = "Uji Mangkuk Zen"
    override val dailyQuoteEnabledToast = "Pengingat kutipan harian diaktifkan (%s)"
    override val dailyQuoteDisabledToast = "Pengingat kutipan harian dinonaktifkan"
    override val meditationReminderEnabledToast = "Pengingat meditasi diaktifkan (%s)"
    override val meditationReminderDisabledToast = "Pengingat meditasi dinonaktifkan"
    override val timeUpdatedToast = "Waktu pengingat diperbarui ke %02d:%02d"
    override val vibrationEnabledToast = "Getaran lembut diaktifkan"
    override val vibrationDisabledToast = "Getaran dinonaktifkan"
    override val testBellTriggeredToast = "🔔 Suara lonceng diputar & notifikasi terkirim"
    override val testBowlTriggeredToast = "🥣 Suara mangkuk Zen diputar & notifikasi terkirim"
    override val notificationPermissionRequiredToast = "Izin notifikasi diperlukan untuk menampilkan pengingat di bilah status"

    override val sectionContentUpdates = "Pembaruan Konten"
    override val contentVersionBadge = "v%s"
    override val contentStatsLabel = "Statistik Konten"
    override val quotesUnit = "Kutipan"
    override val meditationsUnit = "Meditasi"
    override val storageStrategyLabel = "Strategi Penyimpanan"
    override val storageStrategyValue = "Dual Isolated Databases"
    override val distributionChannelLabel = "Kanal Distribusi"
    override val checkUpdatesButton = "Periksa Pembaruan Konten"
    override val checkingUpdatesLabel = "Memeriksa Server..."
    override val updateNowButton = "Perbarui Sekarang (v%s)"

    override val sectionDataBackup = "Cadangan & Privasi Data"
    override val privacyBadge = "100% Privat"
    override val dataBackupDesc = "Data pribadi tersimpan secara lokal dan terisolasi mandiri dari pembaruan konten aplikasi."
    override val personalStatsLabel = "Statistik Pribadi"
    override val favoritesUnit = "Favorit"
    override val historyUnit = "Riwayat"
    override val journalUnit = "Refleksi"
    override val exportJsonButton = "Ekspor JSON"
    override val restoreButton = "Pulihkan"

    override val sectionAbout = "Tentang SoulQuote"
    override val aboutVersion = "SoulQuote v${com.soulquote.app.BuildConfig.VERSION_NAME} • Inspirasi & Meditasi Offline"
    override val aboutDescription = "Dibuat dengan keheningan, privasi, dan penyimpanan data lokal yang aman."
    override val privacyPolicyButton = "Kebijakan Privasi (Privacy Policy)"
    override val disclaimerButton = "Penafian & Sumber Konten (Disclaimer)"
    override val disclaimerTitle = "Penafian & Sumber Konten"
    override val disclaimerBody = "Aplikasi SoulQuote dikembangkan secara mandiri dan BUKAN merupakan aplikasi resmi dari Bunda Arsaningsih maupun organisasi/perusahaan SOUL. Tidak ada ikatan kerja sama resmi, afiliasi, kemitraan, ataupun sponsor antara pengembang aplikasi ini dan pihak Bunda Arsaningsih / organisasi SOUL.\n\nAplikasi ini dibuat semata-mata sebagai sarana pembantu yang terinspirasi oleh materi dan ceramah di Kanal YouTube Resmi Bunda Arsaningsih. Seluruh kutipan bijak dan panduan meditasi bersumber serta dirangkum dari tayangan publik YouTube tersebut, dengan tujuan tulus untuk mempermudah masyarakat dalam melatih keheningan batin dan meditasi SOUL Reflection setiap hari.\n\nHak cipta atas rekaman, naskah renungan, dan ajaran orisinal sepenuhnya tetap menjadi milik Bunda Arsaningsih. Dukung selalu karya asli beliau dengan menonton dan berlangganan di Kanal YouTube Resmi Bunda Arsaningsih."
    override val disclaimerVisitYoutube = "Buka YouTube Bunda Arsaningsih"
    override val rateAppButton = "Beri Rating di Play Store"
    override val ratingPromptTitle = "Suka Menggunakan SoulQuote?"
    override val ratingPromptBody = "Dukungan dan ulasan bintang 5 Anda di Google Play Store sangat berarti untuk membantu kami terus menghadirkan ketenangan dan inspirasi bagi lebih banyak jiwa."
    override val ratingPromptRateButton = "Beri Rating Sekarang"
    override val ratingPromptLaterButton = "Nanti Saja"
    override val ratingNotificationTitle = "SoulQuote • Beri Rating di Play Store"
    override val ratingNotificationMessage = "Bagikan pengalaman damaimu bersama SoulQuote dengan memberikan ulasan di Google Play Store."
    override val splashTagline = "Ruang Hening & Kebijaksanaan Jiwa"
    override val splashMindfulBreath = "Tarik napas perlahan... Hadir seutuhnya di saat ini."

    // Meditation
    override val meditationTitle = "Meditasi"
    override val meditationSubtitle = "Keheningan & ketenangan batin"
    override val tabVoiceGuide = "Panduan Suara"
    override val tabNatureSounds = "Suara Alam"
    override val searchMeditationPlaceholder = "Cari meditasi atau instruktur..."
    override val filterAll = "Semua"
    override val offlineBadge = "Offline"
    override val playLabel = "Putar"
    override val pauseLabel = "Jeda"
    override val minutesUnit = "Menit"
    override val audioOfflineSaved = "Audio berhasil disimpan untuk offline!"
    override val audioOfflineDeleted = "Audio offline berhasil dihapus."
    override val audioDownloading = "Mengunduh audio meditasi..."
    override val audioDownloadFailed = "Gagal mengunduh audio. Periksa koneksi internet."

    // Studio
    override val studioTitle = "Studio Kutipan"
    override val studioSubtitle = "Kreasikan kartu kutipan estetik"
    override val saveImageButton = "Simpan Gambar"
    override val shareButton = "Bagikan"
    override val chooseThemeTitle = "Pilih Tema Desain"
    override val imageSavedSuccess = "Gambar berhasil disimpan ke galeri"

    // Extra Home Strings
    override val guidedMeditationTitle = "Panduan Meditasi"
    override val guidedMeditationDesc = "Heningkan pikiran dengan panduan nafas mindful"
    override val ambientSoundscapesTitle = "Lanskap Suara Alam"
    override val ambientSoundscapesDesc = "Hujan, ombak, hutan & api unggun untuk fokus dan tidur"
    override val loadingDailyQuote = "Memuat refleksi inspiratif..."
    override val openJournalButton = "Buka Jurnal Refleksi"
    override val savedBadge = "Tersimpan"
    override val shareQuoteTitle = "Bagikan Kutipan"

    // Extra Meditation Strings
    override val emptyMeditationList = "Tidak ada sesi meditasi yang sesuai."
    override val instructorPrefix = "Instruktur: %s"
    override val availableOfflineDesc = "Tersedia Offline"
    override val downloadOfflineDesc = "Unduh Offline"
    override val chapterDiscourse = "Pencerahan"
    override val chapterMeditation = "Mulai Meditasi (%s)"
    override val watchOnYouTube = "Tonton Video di YouTube"
    override val watchOnYouTubeAtMeditation = "Tonton di YouTube (Mulai Meditasi %s)"
    override val meditationPhaseDiscourse = "Fase: Pencerahan & Kesadaran"
    override val meditationPhasePractice = "Fase: Meditasi Hening"

    // Extra Settings Strings
    override val closeButton = "Tutup"
    override val updatePackageTitle = "Paket Pembaruan v%s"
    override val newQuotesCount = "+%d kutipan baru ditambahkan"
    override val newMeditationsCount = "+%d sesi meditasi baru ditambahkan"
    override val applyingUpdateLabel = "Menerapkan pembaruan..."
    override val shareBackupTitle = "Simpan / Bagikan Cadangan Data"

    // Journal Strings
    override val journalDialogTitle = "Jurnal Mindful & Refleksi"
    override val tabTodayReflection = "Refleksi Hari Ini"
    override val tabHistoryTitle = "Riwayat (%d)"
    override val howIsYourMoodTitle = "Bagaimana suasana hatimu saat ini?"
    override val mindfulPromptHeader = "Pertanyaan Hening:"
    override val changePromptButton = "Ganti pertanyaan"
    override val journalPlaceholder = "Tuliskan refleksi singkat, rasa syukur, atau pelepasan beban batinmu..."
    override val savedTodayHeader = "Tersimpan hari ini (%d catatan):"
    override val saveReflectionButton = "Simpan Refleksi Hening"
    override val updateReflectionButton = "Perbarui Refleksi"
    override val cancelEditButton = "Batal Ubah"
    override val editingReflectionBanner = "Sedang mengubah refleksi yang dipilih"
    override val emptyHistoryMessage = "Belum ada catatan refleksi.\nMulai tulis rasa syukur pertamamu hari ini!"
    override val deleteEntryButton = "Hapus"
    override val editEntryButton = "Ubah"

    // Moods
    override val moodPeaceful = "Damai"
    override val moodGrateful = "Bersyukur"
    override val moodCalm = "Tenang"
    override val moodEnergetic = "Bersemangat"
    override val moodTired = "Lelah"
    override val moodAnxious = "Gelisah"
}

object EnglishStrings : AppStrings {
    // Navigation
    override val navHome = "Home"
    override val navExplore = "Explore"
    override val navMeditation = "Meditate"
    override val navStudio = "Studio"
    override val navSettings = "Settings"
    override val navFavorites = "Favorites"

    // Home
    override val greetingMorning = "Good Morning"
    override val greetingAfternoon = "Good Afternoon"
    override val greetingEvening = "Good Evening"
    override val greetingNight = "Good Night"
    override val todaysReflection = "Today's Reflection"
    override val innerReflectionMood = "Soul Reflection & Mood"
    override val writeReflection = "Write Today's Reflection"
    override val editReflection = "Edit Reflection"
    override val daysUnit = "Days"
    override val themesOfWisdom = "Themes of Wisdom"
    override val seeAll = "See All"
    override val defaultJournalPrompt = "What brings peace to your soul today?"

    // Settings
    override val settingsTitle = "Settings"
    override val settingsSubtitle = "Customization & Preferences"
    override val sectionLanguage = "Language"
    override val languageSubtitle = "Choose application display language"
    override val indonesianOption = "Bahasa Indonesia (Default)"
    override val englishOption = "English"
    override val languageChangedToast = "Language changed to English"

    override val sectionNotifications = "Reminders & Notifications"
    override val dailyQuoteTitle = "Daily Quote"
    override val scheduledAt = "Scheduled at %s"
    override val changeTimeButton = "Change Time (%02d:%02d)"
    override val meditationReminderTitle = "Meditation Reminder"
    override val gentleVibrationTitle = "Gentle Vibration"
    override val gentleVibrationDesc = "Haptic pulse with notification sound"
    override val testNotificationsTitle = "Sound & Notification Test"
    override val testBellButton = "Test Bell"
    override val testBowlButton = "Test Zen Bowl"
    override val dailyQuoteEnabledToast = "Daily quote reminder enabled (%s)"
    override val dailyQuoteDisabledToast = "Daily quote reminder disabled"
    override val meditationReminderEnabledToast = "Meditation reminder enabled (%s)"
    override val meditationReminderDisabledToast = "Meditation reminder disabled"
    override val timeUpdatedToast = "Reminder time updated to %02d:%02d"
    override val vibrationEnabledToast = "Gentle vibration enabled"
    override val vibrationDisabledToast = "Vibration disabled"
    override val testBellTriggeredToast = "🔔 Bell sound played & notification sent"
    override val testBowlTriggeredToast = "🥣 Zen bowl sound played & notification sent"
    override val notificationPermissionRequiredToast = "Notification permission required to display reminders in status bar"

    override val sectionContentUpdates = "Content Updates"
    override val contentVersionBadge = "v%s"
    override val contentStatsLabel = "Content Statistics"
    override val quotesUnit = "Quotes"
    override val meditationsUnit = "Meditations"
    override val storageStrategyLabel = "Storage Strategy"
    override val storageStrategyValue = "Dual Isolated Databases"
    override val distributionChannelLabel = "Distribution Channel"
    override val checkUpdatesButton = "Check for Content Updates"
    override val checkingUpdatesLabel = "Checking Server..."
    override val updateNowButton = "Update Now (v%s)"

    override val sectionDataBackup = "Data Backup & Privacy"
    override val privacyBadge = "100% Private"
    override val dataBackupDesc = "Personal data is stored locally and isolated from application content updates."
    override val personalStatsLabel = "Personal Statistics"
    override val favoritesUnit = "Favorites"
    override val historyUnit = "History"
    override val journalUnit = "Reflections"
    override val exportJsonButton = "Export JSON"
    override val restoreButton = "Restore"

    override val sectionAbout = "About SoulQuote"
    override val aboutVersion = "SoulQuote v${com.soulquote.app.BuildConfig.VERSION_NAME} • Offline Inspiration & Meditation"
    override val aboutDescription = "Crafted with serenity, privacy, and secure local data persistence."
    override val privacyPolicyButton = "Privacy Policy"
    override val disclaimerButton = "Disclaimer & Content Attribution"
    override val disclaimerTitle = "Disclaimer & Attribution"
    override val disclaimerBody = "SoulQuote is an independently developed application and is NOT an official app of Bunda Arsaningsih or the SOUL organization/company. There is no official affiliation, partnership, sponsorship, or endorsement between the developer and Bunda Arsaningsih or SOUL entities.\n\nThis app was created as a personal mindfulness companion inspired by the teachings and public video content on Bunda Arsaningsih's official YouTube Channel. All wisdom quotes and guided meditation resources are referenced from these public videos to help users easily practice daily SOUL Reflection and inner stillness.\n\nAll original copyrights and intellectual property remain solely with Bunda Arsaningsih. We encourage users to support her official work by subscribing to and following the official Bunda Arsaningsih YouTube channel."
    override val disclaimerVisitYoutube = "Visit Bunda Arsaningsih on YouTube"
    override val rateAppButton = "Rate on Google Play"
    override val ratingPromptTitle = "Enjoying SoulQuote?"
    override val ratingPromptBody = "Your 5-star rating and review on Google Play Store will help us bring mindfulness, peace, and daily wisdom to more souls."
    override val ratingPromptRateButton = "Rate on Play Store"
    override val ratingPromptLaterButton = "Maybe Later"
    override val ratingNotificationTitle = "SoulQuote • Rate on Google Play"
    override val ratingNotificationMessage = "Share your peaceful experience with SoulQuote by leaving a rating on Google Play."
    override val splashTagline = "Sanctuary for Mindfulness & Inner Peace"
    override val splashMindfulBreath = "Breathe in gently... Be fully present in this moment."

    // Meditation
    override val meditationTitle = "Meditation"
    override val meditationSubtitle = "Inner peace & mindful stillness"
    override val tabVoiceGuide = "Voice Guide"
    override val tabNatureSounds = "Nature Sounds"
    override val searchMeditationPlaceholder = "Search meditation or guide..."
    override val filterAll = "All"
    override val offlineBadge = "Offline"
    override val playLabel = "Play"
    override val pauseLabel = "Pause"
    override val minutesUnit = "Minutes"
    override val audioOfflineSaved = "Audio saved for offline listening!"
    override val audioOfflineDeleted = "Offline audio successfully removed."
    override val audioDownloading = "Downloading meditation audio..."
    override val audioDownloadFailed = "Failed to download audio. Check internet connection."

    // Studio
    override val studioTitle = "Quote Studio"
    override val studioSubtitle = "Create aesthetic quote cards"
    override val saveImageButton = "Save Image"
    override val shareButton = "Share"
    override val chooseThemeTitle = "Choose Design Theme"
    override val imageSavedSuccess = "Image saved to gallery"

    // Extra Home Strings
    override val guidedMeditationTitle = "Guided Meditation"
    override val guidedMeditationDesc = "Calm your mind with mindful breathing guidance"
    override val ambientSoundscapesTitle = "Atmospheric Soundscapes"
    override val ambientSoundscapesDesc = "Rain, waves, forest & campfire for focus and sleep"
    override val loadingDailyQuote = "Loading inspiring reflection..."
    override val openJournalButton = "Open Reflection Journal"
    override val savedBadge = "Saved"
    override val shareQuoteTitle = "Share Quote"

    // Extra Meditation Strings
    override val emptyMeditationList = "No meditation sessions match your search."
    override val instructorPrefix = "Instructor: %s"
    override val availableOfflineDesc = "Available Offline"
    override val downloadOfflineDesc = "Download Offline"
    override val chapterDiscourse = "Discourse"
    override val chapterMeditation = "Start Meditation (%s)"
    override val watchOnYouTube = "Watch Video on YouTube"
    override val watchOnYouTubeAtMeditation = "Watch on YouTube (Start at %s)"
    override val meditationPhaseDiscourse = "Phase: Discourse & Awareness"
    override val meditationPhasePractice = "Phase: Guided Stillness"

    // Extra Settings Strings
    override val closeButton = "Close"
    override val updatePackageTitle = "Update Package v%s"
    override val newQuotesCount = "+%d new quotes added"
    override val newMeditationsCount = "+%d new meditation sessions added"
    override val applyingUpdateLabel = "Applying update..."
    override val shareBackupTitle = "Save / Share Data Backup"

    // Journal Strings
    override val journalDialogTitle = "Mindful Journal & Reflection"
    override val tabTodayReflection = "Today's Reflection"
    override val tabHistoryTitle = "History (%d)"
    override val howIsYourMoodTitle = "How is your mood right now?"
    override val mindfulPromptHeader = "Mindful Prompt:"
    override val changePromptButton = "Change prompt"
    override val journalPlaceholder = "Write your brief reflection, gratitude, or inner release..."
    override val savedTodayHeader = "Saved today (%d entries):"
    override val saveReflectionButton = "Save Mindful Reflection"
    override val updateReflectionButton = "Update Reflection"
    override val cancelEditButton = "Cancel Edit"
    override val editingReflectionBanner = "Editing selected reflection"
    override val emptyHistoryMessage = "No reflection entries yet.\nStart writing your first gratitude today!"
    override val deleteEntryButton = "Delete"
    override val editEntryButton = "Edit"

    // Moods
    override val moodPeaceful = "Peaceful"
    override val moodGrateful = "Grateful"
    override val moodCalm = "Calm"
    override val moodEnergetic = "Energetic"
    override val moodTired = "Tired"
    override val moodAnxious = "Anxious"
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { IndonesianStrings }
