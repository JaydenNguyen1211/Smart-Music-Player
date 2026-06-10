package mazentas.playme.music.model.smartplaylist

import androidx.annotation.DrawableRes
import mazentas.playme.music.R
import mazentas.playme.music.model.AbsCustomPlaylist

abstract class AbsSmartPlaylist(
    name: String,
    @DrawableRes val iconRes: Int = R.drawable.ic_queue_music
) : AbsCustomPlaylist(
    id = PlaylistIdGenerator(name, iconRes),
    name = name
)