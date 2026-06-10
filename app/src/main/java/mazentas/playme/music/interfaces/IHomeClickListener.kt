package mazentas.playme.music.interfaces

import mazentas.playme.music.model.Album
import mazentas.playme.music.model.Artist
import mazentas.playme.music.model.Genre

interface IHomeClickListener {
    fun onAlbumClick(album: Album)

    fun onArtistClick(artist: Artist)

    fun onGenreClick(genre: Genre)
}