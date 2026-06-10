package mazentas.playme.music.interfaces

import android.view.View
import mazentas.playme.music.model.Genre

interface IGenreClickListener {
    fun onClickGenre(genre: Genre, view: View)
}