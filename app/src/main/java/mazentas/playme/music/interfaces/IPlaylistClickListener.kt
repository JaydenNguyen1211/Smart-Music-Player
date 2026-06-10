package mazentas.playme.music.interfaces

import android.view.View
import mazentas.playme.music.db.PlaylistWithSongs

interface IPlaylistClickListener {
    fun onPlaylistClick(playlistWithSongs: PlaylistWithSongs, view: View)
}