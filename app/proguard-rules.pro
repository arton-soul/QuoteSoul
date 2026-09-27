# Proguard / R8 Optimization Rules for SoulQuote (com.dearyoti.soulquote)

# Preserve annotations and type signatures
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable

# Room Database Architecture
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.migration.Migration
-dontwarn androidx.room.paging.**

# Developer Content & User Data Models / Entities (JSON & SQLite serialization)
-keep class com.soulquote.app.data.local.entity.** { *; }
-keep class com.soulquote.app.data.remote.model.** { *; }
-keep class com.soulquote.app.domain.model.** { *; }
-keep class com.soulquote.app.core.backup.** { *; }
-keep class com.soulquote.app.core.content.** { *; }

# Media3 ExoPlayer for guided meditation playback
-keep class androidx.media3.exoplayer.** { *; }
-keep class androidx.media3.common.** { *; }
-keep class androidx.media3.session.** { *; }
-keep class androidx.media3.datasource.** { *; }
-dontwarn androidx.media3.**

# Android Components: AppWidget, Receivers & FileProvider
-keep class com.soulquote.app.core.widget.** { *; }
-keep class com.soulquote.app.core.notification.receiver.** { *; }
-keep class androidx.core.content.FileProvider { *; }

# Kotlin Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Android Jetpack Compose
-keepclassmembers class * extends androidx.compose.ui.Modifier { *; }
-dontwarn androidx.compose.**
