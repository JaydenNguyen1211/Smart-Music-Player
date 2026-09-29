/*
 * Copyright (c) 2026 Playme Team.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package mazentas.playme.music.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import mazentas.playme.music.R
import mazentas.playme.music.activities.DriveModeActivity
import mazentas.playme.music.activities.WhatsNewFragment
import mazentas.playme.music.extensions.showToast
import mazentas.playme.music.helper.MusicPlayerRemote.audioSessionId

object NavigationUtil {
    fun gotoDriveMode(activity: Activity) {
        activity.startActivity(
            Intent(activity, DriveModeActivity::class.java), null
        )
    }

    fun gotoWhatNews(activity: FragmentActivity) {
        val changelogBottomSheet = WhatsNewFragment()
        changelogBottomSheet.show(activity.supportFragmentManager, WhatsNewFragment.TAG)
    }

    fun openEqualizer(activity: Activity) {
        stockEqualizer(activity)
    }

    private fun stockEqualizer(activity: Activity) {
        val sessionId = audioSessionId
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            activity.showToast(R.string.no_audio_ID, Toast.LENGTH_LONG)
        } else {
            try {
                val effects = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId)
                effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                activity.startActivityForResult(effects, 0)
            } catch (notFound: ActivityNotFoundException) {
                activity.showToast(R.string.no_equalizer)
            }
        }
    }
}