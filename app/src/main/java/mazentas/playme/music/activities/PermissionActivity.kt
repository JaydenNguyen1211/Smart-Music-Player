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
package mazentas.playme.music.activities

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import code.name.monkey.appthemehelper.util.VersionUtils
import mazentas.playme.music.R
import mazentas.playme.music.activities.base.AbsMusicServiceActivity
import mazentas.playme.music.databinding.ActivityPermissionBinding
import mazentas.playme.music.extensions.*

class PermissionActivity : AbsMusicServiceActivity() {
    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColorAuto()
        setTaskDescriptionColorAuto()
        setupTitle()

        binding.storagePermission.setButtonClick {
            requestPermissions()
        }

        if (VersionUtils.hasS()) {
            binding.bluetoothPermission.show()
            binding.bluetoothPermission.setButtonClick {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(BLUETOOTH_CONNECT),
                    BLUETOOTH_PERMISSION_REQUEST
                )
            }
        }

        binding.finish.accentBackgroundColor()
        binding.finish.setOnClickListener {
            if (hasPermissions()) {
                startActivity(
                    Intent(this, MainActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
                remove()
            }
        })
    }

    private fun setupTitle() {
        val color = accentColor()
        val hexColor = String.format("#%06X", 0xFFFFFF and color)
        val appName =
            getString(
                R.string.message_welcome,
                "<b>Smart <span  style='color:$hexColor';>Music</span></b>"
            )
                .parseAsHtml()
        binding.appNameText.text = appName
    }

    override fun onResume() {
        super.onResume()
        binding.finish.isEnabled = hasStoragePermission()
        if (hasStoragePermission()) {
            binding.storagePermission.checkImage.isVisible = true
            binding.storagePermission.checkImage.imageTintList =
                ColorStateList.valueOf(accentColor())
        }
        if (VersionUtils.hasS()) {
            if (hasBluetoothPermission()) {
                binding.bluetoothPermission.checkImage.isVisible = true
                binding.bluetoothPermission.checkImage.imageTintList =
                    ColorStateList.valueOf(accentColor())
            }
        }
    }

    private fun hasStoragePermission(): Boolean {
        return hasPermissions()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun hasBluetoothPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

}
