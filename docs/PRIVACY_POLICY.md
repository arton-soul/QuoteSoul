# Privacy Policy for SoulQuote

**Effective Date:** September 27, 2026  
**Last Updated:** September 27, 2026  
**Application Name:** SoulQuote  
**Application ID:** `com.dearyoti.soulquote`  
**Developer:** Made Sudiartono (SoulQuote)  
**Official Privacy Policy URL:** [https://dearyoti.blogspot.com/p/privacy-policy-for-soulquote.html](https://dearyoti.blogspot.com/p/privacy-policy-for-soulquote.html)  

---

## 1. Overview & Privacy Philosophy
Welcome to **SoulQuote** ("we", "our", or "the app"). SoulQuote is an offline-first personal inspiration, daily quote reflection, guided meditation, background ambient soundscape, and quote card creation application.

We believe that peace of mind begins with personal privacy. Our core philosophy is **Zero Unconsented Data Collection**: SoulQuote is built from the ground up to respect your personal boundaries. We do not require you to create an account, log in, or share any personal identity.

---

## 2. Information We Collect (or Rather, Do Not Collect)

### 2.1 Personal Information
- **We DO NOT collect, store, share, or sell any personally identifiable information (PII)** such as your name, email address, phone number, physical address, contacts, device ID, advertising ID, or biometric data.
- The app operates completely without user accounts or cloud authentication.

### 2.2 User-Generated Content & Journal Reflections
- Any content you create within SoulQuote — including **mindful journal reflections, mood check-ins, favorite quotes, meditation history, and streak counters** — is stored **100% locally on your device** in a private SQLite database (`soulquote_user.db`).
- This data never leaves your device unless you explicitly choose to export a backup file via the built-in backup feature.

### 2.3 Third-Party Analytics & Tracking
- SoulQuote contains **NO third-party analytics SDKs** (such as Firebase Analytics, Google Analytics, Facebook SDK, AppsFlyer, or Adjust).
- SoulQuote contains **NO third-party advertising networks** and does not track your behavior across other apps or websites.

---

## 3. Device Permissions & How They Are Used

SoulQuote requests only the minimum device permissions necessary to deliver its core features:

| Permission | Purpose / Rationale |
| :--- | :--- |
| **`INTERNET` & `ACCESS_NETWORK_STATE`** | Used exclusively to check for and download optional public content updates (new quotes and guided meditation audio files) from static distribution storage (GitHub / Google Drive) when initiated. |
| **`POST_NOTIFICATIONS`** | Required on Android 13+ to deliver local daily quote inspirations and meditation reminders scheduled by you. |
| **`RECEIVE_BOOT_COMPLETED`** | Used to automatically restore your scheduled daily reminder alarms after your device is rebooted. |
| **`SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`** | Allows the app to trigger daily mindfulness notifications at the exact hour and minute you selected in Settings. |
| **`VIBRATE`** | Provides gentle haptic feedback during meditation sessions and alerts. |
| **Storage / Media Access (`WRITE_EXTERNAL_STORAGE`)** | Used exclusively on legacy Android versions (Android 9 and below) to save your customized quote cards to the device Photo Gallery. On Android 10+, standard Scoped Storage (`MediaStore`) is used without requiring broad storage permissions. |
| **Photo Picker (`PickVisualMedia`)** | When you choose a custom background image in Quote Studio, the Android system Photo Picker is used. SoulQuote only gains access to the single photo you explicitly tap and select; it cannot view or access the rest of your photo gallery. |

---

## 4. Third-Party Services & Hosting

The app may interact with the following third-party static services:
- **GitHub / Google Drive**: Used strictly as static content distribution storage to deliver optional quote packages and guided audio files via standard HTTPS GET requests. No user credentials or personal parameters are transmitted during these requests.

---

## 5. Data Retention, Backup, and Deletion

- **Local Retention**: Your settings, favorites, and journal entries remain on your device for as long as SoulQuote is installed.
- **Data Export (Backup)**: You can create a local JSON backup file of all your personal data at any time via **Settings > Cadangan & Privasi Data > Ekspor Cadangan Data**.
- **Data Deletion**: You can permanently delete all data stored by SoulQuote at any time by:
  1. Opening **Device Settings > Apps > SoulQuote > Storage > Clear Data / Clear Storage**, or
  2. Uninstalling the SoulQuote application from your device.

---

## 6. Children's Privacy (COPPA Compliance)

SoulQuote does not address anyone under the age of 13, nor do we knowingly collect personally identifiable information from children under 13. Since the app does not collect personal data from any user regardless of age, it is safe for all audiences and complies with the Children's Online Privacy Protection Act (COPPA) and Google Play Families Policy.

---

## 7. Security of Your Data

Because all personal reflection and journal data is stored locally in your device's isolated application sandbox, the security of your data is directly protected by Android's operating system security and your device lock screen (PIN, password, or biometrics).

---

## 8. Changes to This Privacy Policy

We may update our Privacy Policy from time to time. Any changes will be posted on this page with an updated "Effective Date". We encourage you to review this page periodically for any changes.

---

## 9. Contact Us

If you have any questions, feedback, or concerns regarding this Privacy Policy or the privacy practices of SoulQuote, please feel free to contact us:

- **Developer:** Made Sudiartono
- **Email:** `support@soulquote.app` *(or your primary support email)*
- **GitHub:** [https://github.com/arton-soul/QuoteSoul](https://github.com/arton-soul/QuoteSoul)
- **Application:** SoulQuote (`com.dearyoti.soulquote`)
