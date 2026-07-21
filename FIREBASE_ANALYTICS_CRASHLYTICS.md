# Firebase Crashlytics & Analytics Instrumentation

This document describes the Firebase Crashlytics and Analytics (screen views + button
clicks) work added to the app.

## 1. What was added

### Crashlytics

| File | Change |
|---|---|
| `build.gradle.kts` (root) | Added the `com.google.firebase.crashlytics` Gradle plugin (v3.0.7), `apply false`. |
| `app/build.gradle.kts` | Applied the `com.google.firebase.crashlytics` plugin and added the `com.google.firebase:firebase-crashlytics` dependency (version resolved via the existing `firebase-bom:34.16.0`). |
| `app/proguard-rules.pro` | Uncommented `-keepattributes SourceFile,LineNumberTable` and `-renamesourcefileattribute SourceFile` so release-build stack traces uploaded to Crashlytics stay readable. |
| `App.kt` | `FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG` — crash reporting is enabled for release builds only, so debug/dev crashes don't pollute the dashboard. |

`google-services.json` was already present in `app/`, and `firebase-analytics` +
the `com.google.gms.google-services` plugin were already wired up, so no Firebase
project setup was needed — this only adds Crashlytics to the existing project.

### Analytics helper

New file: `app/src/main/java/mazentas/playme/music/analytics/AnalyticsHelper.kt`

A single object wrapping both SDKs so every call site is a one-liner:

- `AnalyticsHelper.logScreenView(screenName, screenClass)` — logs a Firebase
  `screen_view` event and leaves a `"screen_view: X"` Crashlytics log breadcrumb,
  plus sets a `current_screen` custom key, so any crash report shows the last
  screen the user was on.
- `AnalyticsHelper.logButtonClick(buttonName, screenName?)` — logs a custom
  `button_click` event (params: `button_name`, optional `screen_name`) and a
  matching Crashlytics breadcrumb.

## 2. Screen-view tracking (automatic, covers every screen)

Rather than adding a call in all 24 activities and 89 fragments individually,
`App.kt` registers:

- an `Application.ActivityLifecycleCallbacks` that logs a screen view on every
  `onActivityResumed`, and
- a `FragmentManager.FragmentLifecycleCallbacks` (registered recursively via
  `registerFragmentLifecycleCallbacks(callbacks, true)`) that logs a screen view
  on every `onFragmentResumed` — this also covers child/nested fragments and
  dialogs.

Effect: **every current and future Activity/Fragment is tracked automatically**,
with no per-screen code needed. `screen_class` is the fully-qualified class name,
`screen_name` is the simple class name (e.g. `PlayerFragment`, `AddToPlaylistDialog`).

Trade-off to be aware of: dialogs and bottom sheets are also logged as "screens"
since they're Fragments too. This is usually useful signal, but if you'd rather
exclude them, filter with `f is DialogFragment` in `App.kt`.

## 3. Button-click tracking (key buttons, per your chosen scope)

The app has ~160 `setOnClickListener` call sites spread across 89 fragments
(20 of them are duplicate play/pause implementations — one per player-UI theme:
classic, card, flat, md3, gradient, etc.). Instead of duplicating a log call in
every theme's copy of the same button, clicks are instrumented at the shared
logic layer they all funnel through, so one change covers every UI theme/screen
that exposes that action:

**`helper/MusicPlayerRemote.kt`** (used by all 20 player-UI themes' playback controls):
- `play` — `resumePlaying()`
- `pause` — `pauseSong()`
- `next` — `playNextSong()`
- `previous` — `back()` / `playPreviousSong()`
- `shuffle` — `toggleShuffleMode()`
- `repeat` — `cycleRepeatMode()`

**`helper/menu/SongMenuHelper.kt`** (single-song context menu — used from Songs,
Albums, Artists, Playlists, Genres, Folders, Search, Queue lists):
logs the clicked menu action's resource name (e.g. `action_share`,
`action_delete_from_device`, `action_add_to_playlist`, `action_play_next`,
`action_add_to_current_playing`, `action_tag_editor`, `action_details`,
`action_go_to_album`, `action_go_to_artist`, `action_add_to_blacklist`,
`action_set_as_ringtone`) tagged with screen `song_menu`.

**`helper/menu/SongsMenuHelper.kt`** (multi-select song actions): same idea,
tagged `songs_menu`.

**`helper/menu/PlaylistMenuHelper.kt`**: `action_play`, `action_play_next`,
`action_add_to_playlist`, `action_add_to_current_playing`,
`action_rename_playlist`, `action_delete_playlist`, `action_save_playlist` —
tagged `playlist_menu`.

**`helper/menu/GenreMenuHelper.kt`**: `action_play`, `action_play_next`,
`action_add_to_playlist`, `action_add_to_current_playing` — tagged `genre_menu`.

### Not covered (out of scope by design)

Per-screen "Play all" / "Shuffle all" header buttons on the Album/Artist/
Playlist/Folder detail screens, and other one-off buttons that aren't behind
a shared helper, were left out — you chose "key buttons only" given the ~160
call sites in the app, so this focused on the highest-traffic, shared actions
(playback transport + the contextual song/playlist/genre menus) rather than
touching every fragment. To add more, call
`AnalyticsHelper.logButtonClick("your_button_name", "your_screen_name")` at the
click site — see any of the files above for the pattern.

## 4. Verification

- `./gradlew :app:compileDebugKotlin` — passes.
- `./gradlew :app:assembleDebug` — passes (confirms the Crashlytics/google-services
  Gradle plugins and new dependency resolve correctly).
- Not verified: an actual on-device run + checking events show up in the Firebase
  console (DebugView) / a forced test crash reaching the Crashlytics dashboard.
  Firebase Analytics events can take a few hours to appear in standard reports;
  use **DebugView** in the Firebase console for near-real-time verification during
  testing (`adb shell setprop debug.firebase.analytics.app mazentas.playme.music`).
