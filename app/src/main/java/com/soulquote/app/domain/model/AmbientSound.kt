package com.soulquote.app.domain.model

import androidx.annotation.RawRes
import com.soulquote.app.R

data class AmbientSound(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    @RawRes val rawResId: Int,
    val category: String
) {
    companion object {
        val DEFAULT_SOUNDS = listOf(
            AmbientSound(
                id = "sound_rain",
                name = "Hujan Rintik",
                description = "Tetesan air hujan lembut membasahi dedaunan, menenangkan pikiran yang gelisah.",
                iconName = "water_drop",
                rawResId = R.raw.ambient_rain,
                category = "nature"
            ),
            AmbientSound(
                id = "sound_ocean",
                name = "Ombak Samudra",
                description = "Irama deburan ombak pasang surut pantai yang damai dan ritmis.",
                iconName = "waves",
                rawResId = R.raw.ambient_ocean,
                category = "nature"
            ),
            AmbientSound(
                id = "sound_forest",
                name = "Hutan Hening",
                description = "Semilir angin lembut di sela pepohonan rimbun diiringi kicauan burung pagi.",
                iconName = "forest",
                rawResId = R.raw.ambient_forest,
                category = "nature"
            ),
            AmbientSound(
                id = "sound_campfire",
                name = "Api Unggun",
                description = "Kehangatan gemeretak kayu bakar di tengah malam yang sunyi dan damai.",
                iconName = "fireplace",
                rawResId = R.raw.ambient_campfire,
                category = "warmth"
            ),
            AmbientSound(
                id = "sound_night",
                name = "Malam Syahdu",
                description = "Irama jangkrik malam dan hawa sejuk menuntun jiwa beristirahat lelap.",
                iconName = "nightlight",
                rawResId = R.raw.ambient_night,
                category = "sleep"
            ),
            AmbientSound(
                id = "sound_zen_bowl",
                name = "Lonceng Tibet (432Hz)",
                description = "Resonansi gelombang harmonik murni mangkuk bernyanyi untuk meditasi mendalam.",
                iconName = "self_improvement",
                rawResId = R.raw.ambient_zen_bowl,
                category = "meditation"
            ),
            AmbientSound(
                id = "sound_pink_noise",
                name = "Derau Lembut (Pink Noise)",
                description = "Frekuensi suara konstan penyeimbang gelombang otak untuk konsentrasi dan tidur.",
                iconName = "graphic_eq",
                rawResId = R.raw.ambient_white_noise,
                category = "focus"
            )
        )
    }
}

data class AmbientPreset(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val soundVolumes: Map<String, Float>
) {
    companion object {
        val DEFAULT_PRESETS = listOf(
            AmbientPreset(
                id = "preset_rainy_evening",
                name = "Malam Hujan Hangat",
                description = "Kombinasi rintik hujan dan kehangatan api unggun.",
                iconName = "water_drop",
                soundVolumes = mapOf(
                    "sound_rain" to 0.75f,
                    "sound_campfire" to 0.45f
                )
            ),
            AmbientPreset(
                id = "preset_forest_sanctuary",
                name = "Suaka Hutan Zen",
                description = "Angin hutan berpadu dengungan resonansi lonceng Tibet.",
                iconName = "forest",
                soundVolumes = mapOf(
                    "sound_forest" to 0.70f,
                    "sound_zen_bowl" to 0.40f
                )
            ),
            AmbientPreset(
                id = "preset_deep_sleep",
                name = "Tidur Lelap Samudra",
                description = "Deburan ombak lembut diringi irama malam syahdu.",
                iconName = "nightlight",
                soundVolumes = mapOf(
                    "sound_ocean" to 0.65f,
                    "sound_night" to 0.35f
                )
            ),
            AmbientPreset(
                id = "preset_pure_focus",
                name = "Fokus & Konsentrasi",
                description = "Derau lembut penyeimbang pikiran dan tetesan hujan.",
                iconName = "graphic_eq",
                soundVolumes = mapOf(
                    "sound_pink_noise" to 0.50f,
                    "sound_rain" to 0.40f
                )
            )
        )
    }
}
