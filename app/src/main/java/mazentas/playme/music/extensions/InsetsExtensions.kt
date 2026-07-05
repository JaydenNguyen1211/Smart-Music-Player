package mazentas.playme.music.extensions

import androidx.core.view.WindowInsetsCompat
import mazentas.playme.music.util.PreferenceUtil
import mazentas.playme.music.util.SmartUtil

fun WindowInsetsCompat?.getBottomInsets(): Int {
    return if (PreferenceUtil.isFullScreenMode) {
        return 0
    } else {
        this?.getInsets(WindowInsetsCompat.Type.systemBars())?.bottom ?: SmartUtil.navigationBarHeight
    }
}
