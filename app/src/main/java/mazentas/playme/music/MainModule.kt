package mazentas.playme.music

import androidx.room.Room
import mazentas.playme.music.auto.AutoMusicProvider
import mazentas.playme.music.cast.SmartWebServer
import mazentas.playme.music.db.MIGRATION_23_24
import mazentas.playme.music.db.SmartDatabase
import mazentas.playme.music.fragments.LibraryViewModel
import mazentas.playme.music.fragments.albums.AlbumDetailsViewModel
import mazentas.playme.music.fragments.artists.ArtistDetailsViewModel
import mazentas.playme.music.fragments.genres.GenreDetailsViewModel
import mazentas.playme.music.fragments.playlists.PlaylistDetailsViewModel
import mazentas.playme.music.model.Genre
import mazentas.playme.music.network.provideDefaultCache
import mazentas.playme.music.network.provideLastFmRest
import mazentas.playme.music.network.provideLastFmRetrofit
import mazentas.playme.music.network.provideOkHttp
import mazentas.playme.music.repository.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {

    factory {
        provideDefaultCache()
    }
    factory {
        provideOkHttp(get(), get())
    }
    single {
        provideLastFmRetrofit(get())
    }
    single {
        provideLastFmRest(get())
    }
}

private val roomModule = module {

    single {
        Room.databaseBuilder(androidContext(), SmartDatabase::class.java, "playlist.db")
            .addMigrations(MIGRATION_23_24)
            .build()
    }

    factory {
        get<SmartDatabase>().playlistDao()
    }

    factory {
        get<SmartDatabase>().playCountDao()
    }

    factory {
        get<SmartDatabase>().historyDao()
    }

    single {
        RealRoomRepository(get(), get(), get())
    } bind RoomRepository::class
}
private val autoModule = module {
    single {
        AutoMusicProvider(
            androidContext(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}
private val mainModule = module {
    single {
        androidContext().contentResolver
    }
    single {
        SmartWebServer(get())
    }
}
private val dataModule = module {
    single {
        RealRepository(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    } bind Repository::class

    single {
        RealSongRepository(get())
    } bind SongRepository::class

    single {
        RealGenreRepository(get(), get())
    } bind GenreRepository::class

    single {
        RealAlbumRepository(get())
    } bind AlbumRepository::class

    single {
        RealArtistRepository(get(), get())
    } bind ArtistRepository::class

    single {
        RealPlaylistRepository(get())
    } bind PlaylistRepository::class

    single {
        RealTopPlayedRepository(get(), get(), get(), get())
    } bind TopPlayedRepository::class

    single {
        RealLastAddedRepository(
            get(),
            get(),
            get()
        )
    } bind LastAddedRepository::class

    single {
        RealSearchRepository(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single {
        RealLocalDataRepository(get())
    } bind LocalDataRepository::class
}

private val viewModules = module {

    viewModel {
        LibraryViewModel(get())
    }

    viewModel { (albumId: Long) ->
        AlbumDetailsViewModel(
            get(),
            albumId
        )
    }

    viewModel { (artistId: Long?, artistName: String?) ->
        ArtistDetailsViewModel(
            get(),
            artistId,
            artistName
        )
    }

    viewModel { (playlistId: Long) ->
        PlaylistDetailsViewModel(
            get(),
            playlistId
        )
    }

    viewModel { (genre: Genre) ->
        GenreDetailsViewModel(
            get(),
            genre
        )
    }
}

val appModules = listOf(mainModule, dataModule, autoModule, viewModules, networkModule, roomModule)