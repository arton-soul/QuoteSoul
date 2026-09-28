# SoulQuote Content Distribution Repository

Repositori ini digunakan sebagai Content Delivery Network (CDN) gratis dan andal via GitHub Raw untuk mendistribusikan kutipan baru, meditasi, dan pembaruan konten aplikasi **SoulQuote** (`com.dearyoti.soulquote`) tanpa perlu merilis ulang APK ke Google Play Store.

## Struktur Direktori

```text
soul/
├── distribution/
│   ├── content_manifest.json          <-- Titik akses utama yang dicek aplikasi
│   ├── content_update_v4.json         <-- Paket pembaruan konten v4
│   └── content_update_v5.json         <-- (Untuk pembaruan berikutnya)
└── README.md
```

## URL Akses Langsung (Raw Endpoint)
Aplikasi SoulQuote secara otomatis membaca URL berikut:
`https://raw.githubusercontent.com/arton-soul/QuoteSoul/main/distribution/content_manifest.json`

## Cara Merilis Pembaruan Konten Baru (Misal Versi 5)

1. **Buat file paket**: `distribution/content_update_v5.json` berisi kutipan baru.
2. **Hitung Ukuran & Checksum**:
   ```powershell
   (Get-Item distribution\content_update_v5.json).Length
   (Get-FileHash distribution\content_update_v5.json -Algorithm SHA256).Hash.ToLower()
   ```
3. **Perbarui `distribution/content_manifest.json`**:
   - Ubah `contentVersion` menjadi `5`.
   - Ubah `packageUrl` menjadi link ke `content_update_v5.json`.
   - Perbarui `packageSizeBytes` dan `packageChecksumSha256`.
   - Tulis `changelog` baru.
4. **Push ke GitHub**:
   ```bash
   git add distribution/
   git commit -m "feat: release content update v5"
   git push origin main
   ```
5. **Selesai!** Semua pengguna SoulQuote di seluruh dunia akan otomatis menerima notifikasi pembaruan saat membuka aplikasi.
