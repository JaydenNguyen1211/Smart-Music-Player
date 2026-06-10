package mazentas.playme.music.util

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.edit
import androidx.core.content.getSystemService
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import code.name.monkey.appthemehelper.util.VersionUtils
import mazentas.playme.music.ADAPTIVE_COLOR_APP
import mazentas.playme.music.ALBUM_ARTISTS_ONLY
import mazentas.playme.music.ALBUM_ART_ON_LOCK_SCREEN
import mazentas.playme.music.ALBUM_COVER_STYLE
import mazentas.playme.music.ALBUM_COVER_TRANSFORM
import mazentas.playme.music.ALBUM_DETAIL_SONG_SORT_ORDER
import mazentas.playme.music.ALBUM_GRID_SIZE
import mazentas.playme.music.ALBUM_GRID_SIZE_LAND
import mazentas.playme.music.ALBUM_GRID_STYLE
import mazentas.playme.music.ALBUM_SONG_SORT_ORDER
import mazentas.playme.music.ALBUM_SORT_ORDER
import mazentas.playme.music.APPBAR_MODE
import mazentas.playme.music.ARTIST_ALBUM_SORT_ORDER
import mazentas.playme.music.ARTIST_DETAIL_SONG_SORT_ORDER
import mazentas.playme.music.ARTIST_GRID_SIZE
import mazentas.playme.music.ARTIST_GRID_SIZE_LAND
import mazentas.playme.music.ARTIST_GRID_STYLE
import mazentas.playme.music.ARTIST_SONG_SORT_ORDER
import mazentas.playme.music.ARTIST_SORT_ORDER
import mazentas.playme.music.AUDIO_FADE_DURATION
import mazentas.playme.music.AUTO_DOWNLOAD_IMAGES_POLICY
import mazentas.playme.music.App
import mazentas.playme.music.BLACK_THEME
import mazentas.playme.music.BLUETOOTH_PLAYBACK
import mazentas.playme.music.BLURRED_ALBUM_ART
import mazentas.playme.music.CAROUSEL_EFFECT
import mazentas.playme.music.CIRCLE_PLAY_BUTTON
import mazentas.playme.music.COLORED_APP_SHORTCUTS
import mazentas.playme.music.CROSS_FADE_DURATION
import mazentas.playme.music.CUSTOM_FONT
import mazentas.playme.music.DESATURATED_COLOR
import mazentas.playme.music.ENABLE_SEARCH_PLAYLIST
import mazentas.playme.music.EXPAND_NOW_PLAYING_PANEL
import mazentas.playme.music.EXTRA_SONG_INFO
import mazentas.playme.music.FILTER_SONG
import mazentas.playme.music.GENERAL_THEME
import mazentas.playme.music.GENRE_SORT_ORDER
import mazentas.playme.music.HOME_ALBUM_GRID_STYLE
import mazentas.playme.music.HOME_ARTIST_GRID_STYLE
import mazentas.playme.music.IGNORE_MEDIA_STORE_ARTWORK
import mazentas.playme.music.INITIALIZED_BLACKLIST
import mazentas.playme.music.KEEP_SCREEN_ON
import mazentas.playme.music.LANGUAGE_NAME
import mazentas.playme.music.LAST_ADDED_CUTOFF
import mazentas.playme.music.LAST_CHANGELOG_VERSION
import mazentas.playme.music.LAST_DIRECTORY
import mazentas.playme.music.LAST_SLEEP_TIMER_VALUE
import mazentas.playme.music.LAST_USED_TAB
import mazentas.playme.music.LIBRARY_CATEGORIES
import mazentas.playme.music.LOCALE_AUTO_STORE_ENABLED
import mazentas.playme.music.LOCK_SCREEN
import mazentas.playme.music.LYRICS_OPTIONS
import mazentas.playme.music.LYRICS_TYPE
import mazentas.playme.music.MANAGE_AUDIO_FOCUS
import mazentas.playme.music.MATERIAL_YOU
import mazentas.playme.music.NEW_BLUR_AMOUNT
import mazentas.playme.music.NEXT_SLEEP_TIMER_ELAPSED_REALTIME
import mazentas.playme.music.NOW_PLAYING_SCREEN_ID
import mazentas.playme.music.PAUSE_HISTORY
import mazentas.playme.music.PAUSE_ON_ZERO_VOLUME
import mazentas.playme.music.PLAYBACK_PITCH
import mazentas.playme.music.PLAYBACK_SPEED
import mazentas.playme.music.PLAYLIST_GRID_SIZE
import mazentas.playme.music.PLAYLIST_GRID_SIZE_LAND
import mazentas.playme.music.PLAYLIST_SORT_ORDER
import mazentas.playme.music.R
import mazentas.playme.music.RECENTLY_PLAYED_CUTOFF
import mazentas.playme.music.REMEMBER_LAST_TAB
import mazentas.playme.music.SAF_SDCARD_URI
import mazentas.playme.music.SAVE_LAST_DIRECTORY
import mazentas.playme.music.SCREEN_ON_LYRICS
import mazentas.playme.music.SHOW_LYRICS
import mazentas.playme.music.SHOW_WHEN_LOCKED
import mazentas.playme.music.SLEEP_TIMER_FINISH_SONG
import mazentas.playme.music.SNOWFALL
import mazentas.playme.music.SONG_GRID_SIZE
import mazentas.playme.music.SONG_GRID_SIZE_LAND
import mazentas.playme.music.SONG_GRID_STYLE
import mazentas.playme.music.SONG_SORT_ORDER
import mazentas.playme.music.START_DIRECTORY
import mazentas.playme.music.SWIPE_ANYWHERE_NOW_PLAYING
import mazentas.playme.music.SWIPE_DOWN_DISMISS
import mazentas.playme.music.TAB_TEXT_MODE
import mazentas.playme.music.TOGGLE_ADD_CONTROLS
import mazentas.playme.music.TOGGLE_FULL_SCREEN
import mazentas.playme.music.TOGGLE_HEADSET
import mazentas.playme.music.TOGGLE_HOME_BANNER
import mazentas.playme.music.TOGGLE_SUGGESTIONS
import mazentas.playme.music.TOGGLE_VOLUME
import mazentas.playme.music.USER_NAME
import mazentas.playme.music.WALLPAPER_ACCENT
import mazentas.playme.music.WHITELIST_MUSIC
import mazentas.playme.music.extensions.getIntRes
import mazentas.playme.music.extensions.getStringOrDefault
import mazentas.playme.music.fragments.AlbumCoverStyle
import mazentas.playme.music.fragments.GridStyle
import mazentas.playme.music.fragments.NowPlayingScreen
import mazentas.playme.music.fragments.folder.FoldersFragment
import mazentas.playme.music.helper.SortOrder.AlbumSongSortOrder
import mazentas.playme.music.helper.SortOrder.AlbumSortOrder
import mazentas.playme.music.helper.SortOrder.ArtistAlbumSortOrder
import mazentas.playme.music.helper.SortOrder.ArtistSongSortOrder
import mazentas.playme.music.helper.SortOrder.ArtistSortOrder
import mazentas.playme.music.helper.SortOrder.GenreSortOrder
import mazentas.playme.music.helper.SortOrder.PlaylistSortOrder
import mazentas.playme.music.helper.SortOrder.SongSortOrder
import mazentas.playme.music.model.CategoryInfo
import mazentas.playme.music.transform.CascadingPageTransformer
import mazentas.playme.music.transform.DepthTransformation
import mazentas.playme.music.transform.HingeTransformation
import mazentas.playme.music.transform.HorizontalFlipTransformation
import mazentas.playme.music.transform.NormalPageTransformer
import mazentas.playme.music.transform.VerticalFlipTransformation
import mazentas.playme.music.transform.VerticalStackTransformer
import mazentas.playme.music.util.theme.ThemeMode
import mazentas.playme.music.views.TopAppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File


object PreferenceUtil {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext())

    val defaultCategories = listOf(
        CategoryInfo(CategoryInfo.Category.Home, true),
        CategoryInfo(CategoryInfo.Category.Songs, true),
        CategoryInfo(CategoryInfo.Category.Albums, true),
        CategoryInfo(CategoryInfo.Category.Artists, true),
        CategoryInfo(CategoryInfo.Category.Playlists, true),
        CategoryInfo(CategoryInfo.Category.Genres, false),
        CategoryInfo(CategoryInfo.Category.Folder, false),
        CategoryInfo(CategoryInfo.Category.Search, false)
    )

    var libraryCategory: List<CategoryInfo>
        get() {
            val gson = Gson()
            val collectionType = object : TypeToken<List<CategoryInfo>>() {}.type

            val data = sharedPreferences.getStringOrDefault(
                LIBRARY_CATEGORIES,
                gson.toJson(defaultCategories, collectionType)
            )
            return try {
                Gson().fromJson(data, collectionType)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                return defaultCategories
            }
        }
        set(value) {
            val collectionType = object : TypeToken<List<CategoryInfo?>?>() {}.type
            sharedPreferences.edit {
                putString(LIBRARY_CATEGORIES, Gson().toJson(value, collectionType))
            }
        }

    fun registerOnSharedPreferenceChangedListener(
        listener: OnSharedPreferenceChangeListener,
    ) = sharedPreferences.registerOnSharedPreferenceChangeListener(listener)


    fun unregisterOnSharedPreferenceChangedListener(
        changeListener: OnSharedPreferenceChangeListener,
    ) = sharedPreferences.unregisterOnSharedPreferenceChangeListener(changeListener)


    val baseTheme get() = sharedPreferences.getStringOrDefault(GENERAL_THEME, "auto")

    fun getGeneralThemeValue(isSystemDark: Boolean): ThemeMode {
        val themeMode: String =
            sharedPreferences.getStringOrDefault(GENERAL_THEME, "auto")
        return if (isBlackMode && isSystemDark && themeMode != "light") {
            ThemeMode.BLACK
        } else {
            if (isBlackMode && themeMode == "dark") {
                ThemeMode.BLACK
            } else {
                when (themeMode) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    "auto" -> ThemeMode.AUTO
                    else -> ThemeMode.AUTO
                }
            }
        }
    }

    var languageCode: String
        get() = sharedPreferences.getString(LANGUAGE_NAME, "auto") ?: "auto"
        set(value) = sharedPreferences.edit {
            putString(LANGUAGE_NAME, value)
        }

    var isLocaleAutoStorageEnabled: Boolean
        get() = sharedPreferences.getBoolean(
            LOCALE_AUTO_STORE_ENABLED,
            false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(LOCALE_AUTO_STORE_ENABLED, value)
        }

    var Fragment.userName
        get() = sharedPreferences.getString(
            USER_NAME,
            getString(R.string.user_name)
        )
        set(value) = sharedPreferences.edit {
            putString(USER_NAME, value)
        }

    var safSdCardUri
        get() = sharedPreferences.getStringOrDefault(SAF_SDCARD_URI, "")
        set(value) = sharedPreferences.edit {
            putString(SAF_SDCARD_URI, value)
        }

    private val autoDownloadImagesPolicy
        get() = sharedPreferences.getStringOrDefault(
            AUTO_DOWNLOAD_IMAGES_POLICY,
            "only_wifi"
        )

    var albumArtistsOnly
        get() = sharedPreferences.getBoolean(
            ALBUM_ARTISTS_ONLY,
            false
        )
        set(value) = sharedPreferences.edit { putBoolean(ALBUM_ARTISTS_ONLY, value) }

    var albumDetailSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_DETAIL_SONG_SORT_ORDER,
            AlbumSongSortOrder.SONG_TRACK_LIST
        )
        set(value) = sharedPreferences.edit { putString(ALBUM_DETAIL_SONG_SORT_ORDER, value) }

    var artistDetailSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_DETAIL_SONG_SORT_ORDER,
            ArtistSongSortOrder.SONG_A_Z
        )
        set(value) = sharedPreferences.edit { putString(ARTIST_DETAIL_SONG_SORT_ORDER, value) }

    var songSortOrder
        get() = sharedPreferences.getStringOrDefault(
            SONG_SORT_ORDER,
            SongSortOrder.SONG_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(SONG_SORT_ORDER, value)
        }

    var albumSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_SORT_ORDER,
            AlbumSortOrder.ALBUM_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(ALBUM_SORT_ORDER, value)
        }


    var artistSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_SORT_ORDER,
            ArtistSortOrder.ARTIST_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(ARTIST_SORT_ORDER, value)
        }

    val albumSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_SONG_SORT_ORDER,
            AlbumSongSortOrder.SONG_TRACK_LIST
        )

    val artistSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_SONG_SORT_ORDER,
            AlbumSongSortOrder.SONG_TRACK_LIST
        )

    var artistAlbumSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_ALBUM_SORT_ORDER,
            ArtistAlbumSortOrder.ALBUM_YEAR
        )
        set(value) = sharedPreferences.edit {
            putString(ARTIST_ALBUM_SORT_ORDER, value)
        }

    var playlistSortOrder
        get() = sharedPreferences.getStringOrDefault(
            PLAYLIST_SORT_ORDER,
            PlaylistSortOrder.PLAYLIST_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(PLAYLIST_SORT_ORDER, value)
        }

    val genreSortOrder
        get() = sharedPreferences.getStringOrDefault(
            GENRE_SORT_ORDER,
            GenreSortOrder.GENRE_A_Z
        )

    val isIgnoreMediaStoreArtwork
        get() = sharedPreferences.getBoolean(
            IGNORE_MEDIA_STORE_ARTWORK,
            false
        )

    val isVolumeVisibilityMode
        get() = sharedPreferences.getBoolean(
            TOGGLE_VOLUME, false
        )

    var isInitializedBlacklist
        get() = sharedPreferences.getBoolean(
            INITIALIZED_BLACKLIST, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(INITIALIZED_BLACKLIST, value)
        }

    private val isBlackMode
        get() = sharedPreferences.getBoolean(
            BLACK_THEME, false
        )

    val isExtraControls
        get() = sharedPreferences.getBoolean(
            TOGGLE_ADD_CONTROLS, false
        )

    val isHomeBanner
        get() = sharedPreferences.getBoolean(
            TOGGLE_HOME_BANNER, false
        )

    val isScreenOnEnabled get() = sharedPreferences.getBoolean(KEEP_SCREEN_ON, false)

    val isShowWhenLockedEnabled get() = sharedPreferences.getBoolean(SHOW_WHEN_LOCKED, false)

    val isSongInfo get() = sharedPreferences.getBoolean(EXTRA_SONG_INFO, false)

    val isPauseOnZeroVolume get() = sharedPreferences.getBoolean(PAUSE_ON_ZERO_VOLUME, false)

    var isSleepTimerFinishMusic
        get() = sharedPreferences.getBoolean(
            SLEEP_TIMER_FINISH_SONG, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(SLEEP_TIMER_FINISH_SONG, value)
        }

    val isExpandPanel get() = sharedPreferences.getBoolean(EXPAND_NOW_PLAYING_PANEL, false)

    val isHeadsetPlugged
        get() = sharedPreferences.getBoolean(
            TOGGLE_HEADSET, false
        )

    val isAlbumArtOnLockScreen
        get() = sharedPreferences.getBoolean(
            ALBUM_ART_ON_LOCK_SCREEN, true
        )

    val isBluetoothSpeaker
        get() = sharedPreferences.getBoolean(
            BLUETOOTH_PLAYBACK, false
        )

    val isSpeakerDisabled get() = sharedPreferences.getBoolean("disable_speaker", false)

    val isBlurredAlbumArt
        get() = sharedPreferences.getBoolean(
            BLURRED_ALBUM_ART, false
        ) && !VersionUtils.hasR()

    val blurAmount get() = sharedPreferences.getInt(NEW_BLUR_AMOUNT, 25)

    val isCarouselEffect
        get() = sharedPreferences.getBoolean(
            CAROUSEL_EFFECT, false
        )

    var isColoredAppShortcuts
        get() = sharedPreferences.getBoolean(
            COLORED_APP_SHORTCUTS, true
        )
        set(value) = sharedPreferences.edit {
            putBoolean(COLORED_APP_SHORTCUTS, value)
        }

    var isDesaturatedColor
        get() = sharedPreferences.getBoolean(
            DESATURATED_COLOR, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(DESATURATED_COLOR, value)
        }

    val isAdaptiveColor
        get() = sharedPreferences.getBoolean(
            ADAPTIVE_COLOR_APP, false
        )

    val isFullScreenMode
        get() = sharedPreferences.getBoolean(
            TOGGLE_FULL_SCREEN, false
        )

    val isAudioFocusEnabled
        get() = sharedPreferences.getBoolean(
            MANAGE_AUDIO_FOCUS, false
        )

    val isLockScreen get() = sharedPreferences.getBoolean(LOCK_SCREEN, false)

    @Suppress("deprecation")
    fun isAllowedToDownloadMetadata(context: Context): Boolean {
        return when (autoDownloadImagesPolicy) {
            "always" -> true
            "only_wifi" -> {
                val connectivityManager = context.getSystemService<ConnectivityManager>()
                val network = connectivityManager?.activeNetwork
                val capabilities = connectivityManager?.getNetworkCapabilities(network)
                capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }

            "never" -> false
            else -> false
        }
    }


    var lyricsOption
        get() = sharedPreferences.getInt(LYRICS_OPTIONS, 1)
        set(value) = sharedPreferences.edit {
            putInt(LYRICS_OPTIONS, value)
        }

    var songGridStyle: GridStyle
        get() {
            val id: Int = sharedPreferences.getInt(SONG_GRID_STYLE, 0)
            // We can directly use "first" kotlin extension function here but
            // there maybe layout id stored in this so to avoid a crash we use
            // "firstOrNull"
            return GridStyle.values().firstOrNull { gridStyle ->
                gridStyle.id == id
            } ?: GridStyle.Grid
        }
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_STYLE, value.id)
        }

    var albumGridStyle: GridStyle
        get() {
            val id: Int = sharedPreferences.getInt(ALBUM_GRID_STYLE, 0)
            return GridStyle.values().firstOrNull { gridStyle ->
                gridStyle.id == id
            } ?: GridStyle.Grid
        }
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_STYLE, value.id)
        }

    var artistGridStyle: GridStyle
        get() {
            val id: Int = sharedPreferences.getInt(ARTIST_GRID_STYLE, 3)
            return GridStyle.values().firstOrNull { gridStyle ->
                gridStyle.id == id
            } ?: GridStyle.Circular
        }
        set(value) = sharedPreferences.edit {
            putInt(ARTIST_GRID_STYLE, value.id)
        }

    val filterLength get() = sharedPreferences.getInt(FILTER_SONG, 20)

    var lastVersion
        // This was stored as an integer before now it's a long, so avoid a ClassCastException
        get() = try {
            sharedPreferences.getLong(LAST_CHANGELOG_VERSION, 0)
        } catch (e: ClassCastException) {
            sharedPreferences.edit { remove(LAST_CHANGELOG_VERSION) }
            0
        }
        set(value) = sharedPreferences.edit {
            putLong(LAST_CHANGELOG_VERSION, value)
        }

    var lastSleepTimerValue
        get() = sharedPreferences.getInt(
            LAST_SLEEP_TIMER_VALUE,
            30
        )
        set(value) = sharedPreferences.edit {
            putInt(LAST_SLEEP_TIMER_VALUE, value)
        }


    var nextSleepTimerElapsedRealTime
        get() = sharedPreferences.getInt(
            NEXT_SLEEP_TIMER_ELAPSED_REALTIME,
            -1
        )
        set(value) = sharedPreferences.edit {
            putInt(NEXT_SLEEP_TIMER_ELAPSED_REALTIME, value)
        }

    fun themeResFromPrefValue(themePrefValue: String): Int {
        return when (themePrefValue) {
            "light" -> R.style.Theme_SmartMusicPlayer_Light
            "dark" -> R.style.Theme_SmartMusicPlayer
            else -> R.style.Theme_SmartMusicPlayer
        }
    }

    val homeArtistGridStyle: Int
        get() {
            val position = sharedPreferences.getStringOrDefault(
                HOME_ARTIST_GRID_STYLE, "0"
            ).toInt()
            val layoutRes =
                App.getContext().resources.obtainTypedArray(R.array.pref_home_grid_style_layout)
                    .use {
                        it.getResourceId(position, 0)
                    }
            return if (layoutRes == 0) {
                R.layout.item_artist
            } else layoutRes
        }

    val homeAlbumGridStyle: Int
        get() {
            val position = sharedPreferences.getStringOrDefault(
                HOME_ALBUM_GRID_STYLE, "4"
            ).toInt()
            val layoutRes = App.getContext()
                .resources.obtainTypedArray(R.array.pref_home_grid_style_layout).use {
                    it.getResourceId(position, 0)
                }
            return if (layoutRes == 0) {
                R.layout.item_image
            } else layoutRes
        }

    val tabTitleMode: Int
        get() {
            return when (sharedPreferences.getStringOrDefault(
                TAB_TEXT_MODE, "0"
            ).toInt()) {
                0 -> BottomNavigationView.LABEL_VISIBILITY_AUTO
                1 -> BottomNavigationView.LABEL_VISIBILITY_LABELED
                2 -> BottomNavigationView.LABEL_VISIBILITY_SELECTED
                3 -> BottomNavigationView.LABEL_VISIBILITY_UNLABELED
                else -> BottomNavigationView.LABEL_VISIBILITY_LABELED
            }
        }


    var songGridSize
        get() = sharedPreferences.getInt(
            SONG_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_list_columns)
        )
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_SIZE, value)
        }

    var songGridSizeLand
        get() = sharedPreferences.getInt(
            SONG_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land)
        )
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_SIZE_LAND, value)
        }


    var albumGridSize: Int
        get() = sharedPreferences.getInt(
            ALBUM_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_grid_columns)
        )
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_SIZE, value)
        }


    var albumGridSizeLand
        get() = sharedPreferences.getInt(
            ALBUM_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land)
        )
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_SIZE_LAND, value)
        }


    var artistGridSize
        get() = sharedPreferences.getInt(
            ARTIST_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_grid_columns)
        )
        set(value) = sharedPreferences.edit {
            putInt(ARTIST_GRID_SIZE, value)
        }


    var artistGridSizeLand
        get() = sharedPreferences.getInt(
            ARTIST_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land)
        )
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_SIZE_LAND, value)
        }


    var playlistGridSize
        get() = sharedPreferences.getInt(
            PLAYLIST_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_grid_columns)
        )
        set(value) = sharedPreferences.edit {
            putInt(PLAYLIST_GRID_SIZE, value)
        }


    var playlistGridSizeLand
        get() = sharedPreferences.getInt(
            PLAYLIST_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land)
        )
        set(value) = sharedPreferences.edit {
            putInt(PLAYLIST_GRID_SIZE, value)
        }

    var albumCoverStyle: AlbumCoverStyle
        get() {
            val id: Int = sharedPreferences.getInt(ALBUM_COVER_STYLE, 0)
            for (albumCoverStyle in AlbumCoverStyle.values()) {
                if (albumCoverStyle.id == id) {
                    return albumCoverStyle
                }
            }
            return AlbumCoverStyle.Card
        }
        set(value) = sharedPreferences.edit { putInt(ALBUM_COVER_STYLE, value.id) }


    var nowPlayingScreen: NowPlayingScreen
        get() {
            val id: Int = sharedPreferences.getInt(NOW_PLAYING_SCREEN_ID, 0)
            for (nowPlayingScreen in NowPlayingScreen.values()) {
                if (nowPlayingScreen.id == id) {
                    return nowPlayingScreen
                }
            }
            return NowPlayingScreen.Adaptive
        }
        set(value) = sharedPreferences.edit {
            putInt(NOW_PLAYING_SCREEN_ID, value.id)
            // Also set a cover theme for that now playing
            value.defaultCoverTheme?.let { coverTheme -> albumCoverStyle = coverTheme }
        }

    val albumCoverTransform: ViewPager.PageTransformer
        get() {
            val style = sharedPreferences.getStringOrDefault(
                ALBUM_COVER_TRANSFORM,
                "0"
            ).toInt()
            return when (style) {
                0 -> NormalPageTransformer()
                1 -> CascadingPageTransformer()
                2 -> DepthTransformation()
                3 -> HorizontalFlipTransformation()
                4 -> VerticalFlipTransformation()
                5 -> HingeTransformation()
                6 -> VerticalStackTransformer()
                else -> ViewPager.PageTransformer { _, _ -> }
            }
        }

    var startDirectory: File
        get() {
            val folderPath = FoldersFragment.defaultStartDirectory.path
            val filePath: String = sharedPreferences.getStringOrDefault(START_DIRECTORY, folderPath)
            return File(filePath)
        }
        set(value) = sharedPreferences.edit {
            putString(
                START_DIRECTORY,
                FileUtil.safeGetCanonicalPath(value)
            )
        }

    var lastDirectory: File
        get() {
            val folderPath = FoldersFragment.defaultStartDirectory.path
            val filePath: String = sharedPreferences.getStringOrDefault(LAST_DIRECTORY, folderPath)
            return File(filePath)
        }
        set(value) = sharedPreferences.edit {
            putString(
                LAST_DIRECTORY,
                FileUtil.safeGetCanonicalPath(value)
            )
        }

    var saveLastDirectory: Boolean
        get() = sharedPreferences.getBoolean(SAVE_LAST_DIRECTORY, false)
        set(value) = sharedPreferences.edit { putBoolean(SAVE_LAST_DIRECTORY, value) }

    fun getRecentlyPlayedCutoffTimeMillis(): Long {
        val calendarUtil = CalendarUtil()
        val interval: Long = when (sharedPreferences.getString(RECENTLY_PLAYED_CUTOFF, "")) {
            "today" -> calendarUtil.elapsedToday
            "this_week" -> calendarUtil.elapsedWeek
            "past_seven_days" -> calendarUtil.getElapsedDays(7)
            "past_three_months" -> calendarUtil.getElapsedMonths(3)
            "this_year" -> calendarUtil.elapsedYear
            "this_month" -> calendarUtil.elapsedMonth
            else -> calendarUtil.elapsedMonth
        }
        return System.currentTimeMillis() - interval
    }

    val lastAddedCutoff: Long
        get() {
            val calendarUtil = CalendarUtil()
            val interval =
                when (sharedPreferences.getStringOrDefault(LAST_ADDED_CUTOFF, "this_month")) {
                    "today" -> calendarUtil.elapsedToday
                    "this_week" -> calendarUtil.elapsedWeek
                    "past_three_months" -> calendarUtil.getElapsedMonths(3)
                    "this_year" -> calendarUtil.elapsedYear
                    "this_month" -> calendarUtil.elapsedMonth
                    else -> calendarUtil.elapsedMonth
                }
            return (System.currentTimeMillis() - interval) / 1000
        }

    val homeSuggestions: Boolean
        get() = sharedPreferences.getBoolean(
            TOGGLE_SUGGESTIONS,
            true
        )

    val pauseHistory: Boolean
        get() = sharedPreferences.getBoolean(
            PAUSE_HISTORY,
            false
        )

    var audioFadeDuration
        get() = sharedPreferences
            .getInt(AUDIO_FADE_DURATION, 0)
        set(value) = sharedPreferences.edit { putInt(AUDIO_FADE_DURATION, value) }

    var showLyrics: Boolean
        get() = sharedPreferences.getBoolean(SHOW_LYRICS, false)
        set(value) = sharedPreferences.edit { putBoolean(SHOW_LYRICS, value) }

    val rememberLastTab: Boolean
        get() = sharedPreferences.getBoolean(REMEMBER_LAST_TAB, true)

    val enableSearchPlaylist: Boolean
        get() = sharedPreferences.getBoolean(ENABLE_SEARCH_PLAYLIST, true)

    var lastTab: Int
        get() = sharedPreferences
            .getInt(LAST_USED_TAB, 0)
        set(value) = sharedPreferences.edit { putInt(LAST_USED_TAB, value) }

    val isWhiteList: Boolean
        get() = sharedPreferences.getBoolean(WHITELIST_MUSIC, false)

    val crossFadeDuration
        get() = sharedPreferences
            .getInt(CROSS_FADE_DURATION, 0)

    val isCrossfadeEnabled get() = crossFadeDuration > 0

    val materialYou
        get() = sharedPreferences.getBoolean(MATERIAL_YOU, VersionUtils.hasS())

    val isCustomFont
        get() = sharedPreferences.getBoolean(CUSTOM_FONT, false)

    val isSnowFalling
        get() = sharedPreferences.getBoolean(SNOWFALL, false)

    val lyricsType: CoverLyricsType
        get() = if (sharedPreferences.getString(LYRICS_TYPE, "0") == "0") {
            CoverLyricsType.REPLACE_COVER
        } else {
            CoverLyricsType.OVER_COVER
        }

    var playbackSpeed
        get() = sharedPreferences
            .getFloat(PLAYBACK_SPEED, 1F)
        set(value) = sharedPreferences.edit { putFloat(PLAYBACK_SPEED, value) }

    var playbackPitch
        get() = sharedPreferences
            .getFloat(PLAYBACK_PITCH, 1F)
        set(value) = sharedPreferences.edit { putFloat(PLAYBACK_PITCH, value) }

    val appBarMode: TopAppBarLayout.AppBarMode
        get() = if (sharedPreferences.getString(APPBAR_MODE, "1") == "0") {
            TopAppBarLayout.AppBarMode.COLLAPSING
        } else {
            TopAppBarLayout.AppBarMode.SIMPLE
        }

    val wallpaperAccent
        get() = sharedPreferences.getBoolean(
            WALLPAPER_ACCENT,
            VersionUtils.hasOreoMR1() && !VersionUtils.hasS()
        )

    val lyricsScreenOn
        get() = sharedPreferences.getBoolean(SCREEN_ON_LYRICS, false)

    val circlePlayButton
        get() = sharedPreferences.getBoolean(CIRCLE_PLAY_BUTTON, false)

    val swipeAnywhereToChangeSong
        get() = sharedPreferences.getBoolean(SWIPE_ANYWHERE_NOW_PLAYING, true)

    val swipeDownToDismiss
        get() = sharedPreferences.getBoolean(SWIPE_DOWN_DISMISS, true)
}

enum class CoverLyricsType {
    REPLACE_COVER, OVER_COVER
}