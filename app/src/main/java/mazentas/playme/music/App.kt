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
package mazentas.playme.music

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.appthemehelper.util.VersionUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import mazentas.playme.music.analytics.AnalyticsHelper
import mazentas.playme.music.appshortcuts.DynamicShortcutManager
import mazentas.playme.music.billing.BillingManager
import mazentas.playme.music.helper.WallpaperAccentManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    lateinit var billingManager: BillingManager
    private val wallpaperAccentManager = WallpaperAccentManager(this)

    /**
     * Logs a screen_view event the moment any Activity or Fragment (including nested/child
     * fragments and dialogs) becomes visible, so every screen in the app is tracked without
     * having to instrument each one individually.
     */
    private val fragmentScreenViewCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            super.onFragmentResumed(fm, f)
            AnalyticsHelper.logScreenView(f.javaClass.simpleName, f.javaClass.name)
        }
    }

    private val screenViewTracker = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activity is FragmentActivity) {
                activity.supportFragmentManager
                    .registerFragmentLifecycleCallbacks(fragmentScreenViewCallbacks, true)
            }
        }

        override fun onActivityResumed(activity: Activity) {
            AnalyticsHelper.logScreenView(activity.javaClass.simpleName, activity.javaClass.name)
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        registerActivityLifecycleCallbacks(screenViewTracker)

        startKoin {
            androidContext(this@App)
            modules(appModules)
        }
        // default theme
        if (!ThemeStore.isConfigured(this, 4)) {
            ThemeStore.editTheme(this)
                .accentColorRes(R.color.theme_accent)
                .coloredNavigationBar(true)
                .commit()
        }
        wallpaperAccentManager.init()

        if (VersionUtils.hasNougatMR())
            DynamicShortcutManager(this).initDynamicShortcuts()

        billingManager = BillingManager(this)

        // Set Default values for now playing preferences
        // This will reduce startup time for now playing settings fragment as Preference listener of AbsSlidingMusicPanelActivity won't be called
        PreferenceManager.setDefaultValues(this, R.xml.pref_now_playing_screen, false)
    }

    override fun onTerminate() {
        super.onTerminate()
        billingManager.release()
        wallpaperAccentManager.release()
    }

    companion object {
        private var instance: App? = null

        fun getContext(): App {
            return instance!!
        }

        fun isProVersion(): Boolean {
            return BuildConfig.DEBUG || instance?.billingManager!!.isProVersion
        }
    }
}
