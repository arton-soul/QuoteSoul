# SoulQuote — Security & Privacy Architecture

## 1. Principles
SoulQuote follows the principle of **Least Privilege**, **Zero Unnecessary Telemetry**, and **Cryptographic Verification** for all dynamic assets.

---

## 2. Dynamic Content Security
1. **No Executable Code in Content**: Content packages consist exclusively of JSON, SQLite database records, image assets (`.png`, `.webp`), and audio files (`.mp3`, `.ogg`). The app never evaluates remote scripts, DEX files, or classes.
2. **Zip-Slip Prevention**: When decompressing content update archives:
   ```kotlin
   val canonicalDest = targetDir.canonicalPath
   val entryFile = File(targetDir, entry.name)
   if (!entryFile.canonicalPath.startsWith(canonicalDest)) {
       throw SecurityException("Zip slip detected in entry: ${entry.name}")
   }
   ```
3. **SHA-256 Checksum Enforcement**: All downloaded files must match their pre-declared hash in `manifest.json`. Any discrepancy terminates the update and wipes the staging buffer.

---

## 3. Privacy & On-Device Isolation
- **100% Local User Storage**: Personal journals, favorited quotes, meditation stats, and settings are stored strictly in local SQLite and never sent to remote servers.
- **Minimal Permissions**: The app requires only `INTERNET` and `ACCESS_NETWORK_STATE` for optional content sync and `POST_NOTIFICATIONS` for user-configured reminders. No location, contacts, or phone state permissions are requested.
- **No Hardcoded Secrets**: No secret keys, OAuth client secrets, or private credentials are embedded in code or repositories.
