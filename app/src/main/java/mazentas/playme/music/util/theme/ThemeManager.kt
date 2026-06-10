package mazentas.playme.music.util.theme

import android.content.Context
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import mazentas.playme.music.R
import mazentas.playme.music.extensions.generalThemeValue
import mazentas.playme.music.util.PreferenceUtil
import mazentas.playme.music.util.theme.ThemeMode.*

@StyleRes
fun Context.getThemeResValue(): Int =
    if (PreferenceUtil.materialYou) {
        if (generalThemeValue == BLACK) R.style.Theme_SmartMusicPlayer_MD3_Black
        else R.style.Theme_SmartMusicPlayer_MD3
    } else {
        when (generalThemeValue) {
            LIGHT -> R.style.Theme_SmartMusicPlayer_Light
            DARK -> R.style.Theme_SmartMusicPlayer_Base
            BLACK -> R.style.Theme_SmartMusicPlayer_Black
            AUTO -> R.style.Theme_SmartMusicPlayer_FollowSystem
        }
    }

fun Context.getNightMode(): Int = when (generalThemeValue) {
    LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    DARK -> AppCompatDelegate.MODE_NIGHT_YES
    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
}