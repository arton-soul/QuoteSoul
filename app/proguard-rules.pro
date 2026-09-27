# Proguard rules for SoulQuote
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.Dao *;
    @androidx.room.Database *;
    @androidx.room.Entity *;
}
