# Firebase Analytics Events

All events go through `AnalyticsHelper` (`app/src/main/java/mazentas/playme/music/analytics/`).
Only two event names are used; the screen / button is identified by a parameter.

| Event name    | Firebase type | Parameters                                       | Fired when                        |
|---------------|---------------|--------------------------------------------------|-----------------------------------|
| `screen_view` | Recommended   | `screen_name`, `screen_class`                    | An Activity/Fragment/Dialog resumes |
| `button_click`| Custom        | `button_name`, `screen_name` (optional)          | A button/menu item is tapped      |

## How tracking is wired

| Mechanism | File | Covers |
|-----------|------|--------|
| Activity + Fragment lifecycle callbacks | `App.kt` | `screen_view` for every Activity, Fragment, child fragment and `DialogFragment` |
| `ClickTracker` (window-callback tap detector) | `analytics/ClickTracker.kt`, attached from `App.kt` | `button_click` for every tap on a clickable view in any Activity or `DialogFragment` window, plus options-menu selections |
| Popup-menu helpers | `helper/menu/*MenuHelper.kt` | `button_click` for song / songs / playlist / genre overflow-menu items |
| Playback actions | `helper/MusicPlayerRemote.kt` | `button_click` for playback actions, including ones triggered from notification / headset / widgets |

`ClickTracker` counts a gesture as a tap only if the finger moved less than the touch slop and was
released before the long-press timeout, so scrolls and long presses are ignored.

### `button_name` values

The name is chosen in this order:

1. The view's resource id name (e.g. `player_play_pause_button`, `action_search`).
2. Its `contentDescription`.
3. `<parent list id>_item` for un-named list rows (e.g. `recycler_view_item`).
4. The view class name.

Displayed text (song titles, playlist names, ...) is never sent.

Explicit names from `MusicPlayerRemote`: `play`, `pause`, `next`, `previous`, `repeat`, `shuffle`.

Menu-item ids (sent as `button_name`, with `screen_name` set to `song_menu`, `songs_menu`,
`playlist_menu` or `genre_menu` for popup menus):

`action_add_to_blacklist`, `action_add_to_current_playing`, `action_add_to_playlist`, `action_album`,
`action_artist`, `action_cast`, `action_clear_history`, `action_clear_playing_queue`, `action_delete`,
`action_delete_from_device`, `action_delete_playlist`, `action_details`, `action_equalizer`,
`action_go_to_album`, `action_go_to_artist`, `action_go_to_drive_mode`, `action_go_to_genre`,
`action_go_to_lyrics`, `action_grid_size`, `action_grid_size_1` … `action_grid_size_8`, `action_home`,
`action_layout_card`, `action_layout_circular`, `action_layout_colored_card`,
`action_layout_gradient_image`, `action_layout_image`, `action_layout_normal`, `action_layout_type`,
`action_play`, `action_play_next`, `action_playback_speed`, `action_playlist`,
`action_remove_from_playing_queue`, `action_remove_from_playlist`, `action_rename`,
`action_rename_playlist`, `action_reset_artist_image`, `action_save_playing_queue`,
`action_save_playlist`, `action_scan`, `action_search`, `action_set_artist_image`,
`action_set_as_ringtone`, `action_set_as_start_directory`, `action_settings`, `action_share`,
`action_sleep_timer`, `action_song`, `action_sort_order`, `action_sort_order_album`,
`action_sort_order_artist_song_duration`, `action_sort_order_song_duration`,
`action_sort_order_title`, `action_sort_order_title_desc`, `action_sort_order_track_list`,
`action_sort_order_year`, `action_sort_order_year_desc`, `action_tag_editor`,
`action_toggle_favorite`, `action_toggle_lyrics`.

Other button names come from layout view ids (`app/src/main/res/layout`); they are reported as-is.

## `screen_name` values

**Activities:** `MainActivity`, `PermissionActivity`, `DriveModeActivity`, `LockScreenActivity`,
`LicenseActivity`, `SupportDevelopmentActivity`, `BugReportActivity`, `RestoreActivity`,
`SAFGuideActivity`, `AlbumTagEditorActivity`, `SongTagEditorActivity`, `AppShortcutLauncherActivity`

**Library / browse:** `HomeFragment`, `LibraryFragment`, `SongsFragment`, `AlbumsFragment`,
`ArtistsFragment`, `GenresFragment`, `PlaylistsFragment`, `FoldersFragment`, `SearchFragment`,
`AlbumDetailsFragment`, `ArtistDetailsFragment`, `AlbumArtistDetailsFragment`,
`GenreDetailsFragment`, `PlaylistDetailsFragment`, `DetailListFragment`

**Player:** `MiniPlayerFragment`, `PlayerFragment`, `AdaptiveFragment`, `BlurPlayerFragment`,
`CardFragment`, `CardBlurFragment`, `CirclePlayerFragment`, `ClassicPlayerFragment`, `ColorFragment`,
`FitFragment`, `FlatPlayerFragment`, `FullPlayerFragment`, `GradientPlayerFragment`,
`HomePlayerFragment`, `MD3PlayerFragment`, `MaterialFragment`, `PeekPlayerFragment`,
`PlainPlayerFragment`, `SimplePlayerFragment`, `TinyPlayerFragment`, `LockScreenControlsFragment`,
`PlayerAlbumCoverFragment`, `CoverLyricsFragment`, `LyricsFragment`, `VolumeFragment`,
`PlayingQueueFragment`, `PlayingQueueRVFragment` and the matching `*PlaybackControlsFragment`s

**Settings / other:** `SettingsFragment`, `MainSettingsFragment`, `ThemeSettingsFragment`,
`NowPlayingSettingsFragment`, `PersonalizeSettingsFragment`, `ImageSettingFragment`,
`OtherSettingsFragment`, `AudioSettings`, `AboutFragment`, `BackupFragment`, `UserInfoFragment`,
`WhatsNewFragment`

**Dialog fragments:** `AddToPlaylistDialog`, `BlacklistFolderChooserDialog`, `CreatePlaylistDialog`,
`DeletePlaylistDialog`, `DeleteSongsDialog`, `ImportPlaylistDialog`, `PlaybackSpeedDialog`,
`RemoveSongFromPlaylistDialog`, `RenamePlaylistDialog`, `SavePlaylistDialog`, `SleepTimerDialog`,
`SongDetailDialog`, `SongShareDialog`, and the `*PreferenceDialog` classes

## Known gaps

- Dialogs built with `MaterialAlertDialogBuilder` / `materialDialog(...)` (not `DialogFragment`)
  have their own window that is not tracked: no `screen_view`, and their buttons send no
  `button_click`.
- Overflow / popup menus opened from a toolbar are only tracked where a menu helper logs them
  (song, songs, playlist, genre menus).
- Playback buttons tapped in the player fire both the tap (`button_name` = view id) and the
  `MusicPlayerRemote` event (`play`, `next`, ...), so those taps are counted twice.
- Debug builds send analytics too; only Crashlytics is disabled for debug.
