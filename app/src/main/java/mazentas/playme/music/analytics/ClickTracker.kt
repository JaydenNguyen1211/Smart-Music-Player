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

import android.content.res.Resources
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView

/**
 * Tracks every tap on a clickable view (and every options-menu selection) of a window without
 * touching individual click listeners. Wraps the window callback, watches the touch stream and,
 * when a gesture is a genuine tap (no drag, no long press), resolves the clickable view under the
 * finger and reports it through [AnalyticsHelper.logButtonClick].
 *
 * Only view ids / content descriptions are reported, never displayed text, so song titles and
 * other user data never reach analytics.
 */
object ClickTracker {

    /** Wraps [window]'s callback once; safe to call repeatedly for the same window. */
    fun attach(window: Window, screenName: String) {
        val current = window.callback ?: return
        if (current is TrackingCallback) return
        window.callback = TrackingCallback(window, current, screenName)
    }

    private class TrackingCallback(
        private val window: Window,
        private val delegate: Window.Callback,
        private val screenName: String,
    ) : Window.Callback by delegate {

        private var downX = 0f
        private var downY = 0f
        private var downTime = 0L

        override fun dispatchTouchEvent(event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downY = event.rawY
                    downTime = event.eventTime
                }

                MotionEvent.ACTION_UP -> runCatching { reportTap(event) }
            }
            return delegate.dispatchTouchEvent(event)
        }

        private fun reportTap(event: MotionEvent) {
            val decor = window.decorView
            val config = ViewConfiguration.get(decor.context)
            val moved = Math.hypot(
                (event.rawX - downX).toDouble(),
                (event.rawY - downY).toDouble()
            )
            val duration = event.eventTime - downTime
            if (moved > config.scaledTouchSlop || duration >= ViewConfiguration.getLongPressTimeout()) {
                return
            }
            val target = findClickable(decor, event.rawX.toInt(), event.rawY.toInt()) ?: return
            AnalyticsHelper.logButtonClick(nameOf(target), screenName)
        }

        override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
            runCatching {
                val decor = window.decorView
                AnalyticsHelper.logButtonClick(
                    idName(decor, item.itemId) ?: "menu_item_${item.itemId}", screenName
                )
            }
            return delegate.onMenuItemSelected(featureId, item)
        }
    }

    /** Deepest visible clickable view under the given screen coordinates. */
    private fun findClickable(root: View, x: Int, y: Int): View? {
        if (root.visibility != View.VISIBLE) return null
        val loc = IntArray(2)
        root.getLocationOnScreen(loc)
        if (x < loc[0] || x > loc[0] + root.width || y < loc[1] || y > loc[1] + root.height) {
            return null
        }
        if (root is ViewGroup) {
            for (i in root.childCount - 1 downTo 0) {
                findClickable(root.getChildAt(i), x, y)?.let { return it }
            }
        }
        return if (root.isClickable && root.isEnabled) root else null
    }

    /** Stable, PII-free name: view id, else content description, else "<list id>_item". */
    private fun nameOf(view: View): String {
        idName(view, view.id)?.let { return it }
        view.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let { return it }
        var parent = view.parent
        while (parent is View) {
            if (parent is RecyclerView || parent.id != View.NO_ID) {
                idName(parent, parent.id)?.let { return "${it}_item" }
            }
            parent = parent.parent
        }
        return view.javaClass.simpleName
    }

    private fun idName(view: View?, id: Int): String? {
        if (id == View.NO_ID || id == 0) return null
        val resources = view?.resources ?: return null
        return try {
            resources.getResourceEntryName(id)
        } catch (e: Resources.NotFoundException) {
            null
        }
    }
}
