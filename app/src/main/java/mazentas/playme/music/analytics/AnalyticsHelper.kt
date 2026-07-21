/*
 * Copyright (c) 2020 Hemanth Savarla.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package mazentas.playme.music.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import mazentas.playme.music.App

/**
 * Single entry point for Firebase Analytics (screen views + button clicks) and
 * Crashlytics breadcrumbs, so every call site stays a one-liner.
 */
object AnalyticsHelper {

    private const val EVENT_BUTTON_CLICK = "button_click"
    private const val PARAM_SCREEN_NAME = "screen_name"
    private const val PARAM_BUTTON_NAME = "button_name"

    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(App.getContext())
    }

    /**
     * Logs a screen_view event and leaves a Crashlytics breadcrumb so the last
     * visited screen shows up in any subsequent crash report.
     */
    fun logScreenView(screenName: String, screenClass: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundleOf(
            FirebaseAnalytics.Param.SCREEN_NAME to screenName,
            FirebaseAnalytics.Param.SCREEN_CLASS to screenClass,
        ))
        FirebaseCrashlytics.getInstance().log("screen_view: $screenName")
        FirebaseCrashlytics.getInstance().setCustomKey("current_screen", screenName)
    }

    /**
     * Logs a button_click event, optionally tagged with the screen it happened on.
     */
    fun logButtonClick(buttonName: String, screenName: String? = null) {
        val params = Bundle().apply {
            putString(PARAM_BUTTON_NAME, buttonName)
            if (screenName != null) putString(PARAM_SCREEN_NAME, screenName)
        }
        firebaseAnalytics.logEvent(EVENT_BUTTON_CLICK, params)
        FirebaseCrashlytics.getInstance().log("button_click: $buttonName")
    }
}
