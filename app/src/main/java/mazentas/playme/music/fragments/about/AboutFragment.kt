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
package mazentas.playme.music.fragments.about

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import mazentas.playme.music.App
import mazentas.playme.music.Constants
import mazentas.playme.music.R
import mazentas.playme.music.adapter.ContributorAdapter
import mazentas.playme.music.databinding.FragmentAboutBinding
import mazentas.playme.music.extensions.openUrl
import mazentas.playme.music.fragments.LibraryViewModel
import mazentas.playme.music.util.NavigationUtil
import dev.chrisbanes.insetter.applyInsetter
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class AboutFragment : Fragment(R.layout.fragment_about), View.OnClickListener {
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!
    private val libraryViewModel by activityViewModel<LibraryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutBinding.bind(view)
        binding.aboutContent.cardOther.version.setSummary(getAppVersion())
        setUpView()
        loadContributors()

        binding.aboutContent.root.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }
    }

    private fun setUpView() {
        binding.aboutContent.cardSmartInfo.appRate.setOnClickListener(this)
        binding.aboutContent.cardSmartInfo.appTranslation.setOnClickListener(this)
        binding.aboutContent.cardSmartInfo.appShare.setOnClickListener(this)
        binding.aboutContent.cardSmartInfo.donateLink.setOnClickListener(this)
        binding.aboutContent.cardSmartInfo.bugReportLink.setOnClickListener(this)

        binding.aboutContent.cardOther.changelog.setOnClickListener(this)
        binding.aboutContent.cardOther.openSource.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.appTranslation -> openUrl(Constants.TRANSLATE)
            R.id.appRate -> openUrl(Constants.RATE_ON_GOOGLE_PLAY)
            R.id.appShare -> shareApp()
            R.id.donateLink -> NavigationUtil.goToSupportDevelopment(requireActivity())
            R.id.changelog -> NavigationUtil.gotoWhatNews(requireActivity())
            R.id.openSource -> NavigationUtil.goToOpenSource(requireActivity())
            R.id.bugReportLink -> NavigationUtil.bugReport(requireActivity())
        }
    }

    private fun getAppVersion(): String {
        return try {
            val isPro = if (App.isProVersion()) "Pro" else "Free"
            val packageInfo =
                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            "${packageInfo.versionName} $isPro"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "0.0.0"
        }
    }

    private fun shareApp() {
        ShareCompat.IntentBuilder(requireActivity()).setType("text/plain")
            .setChooserTitle(R.string.share_app)
            .setText(String.format(getString(R.string.app_share), requireActivity().packageName))
            .startChooser()
    }

    private fun loadContributors() {
        val contributorAdapter = ContributorAdapter(emptyList())
        binding.aboutContent.cardCredit.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            adapter = contributorAdapter
        }
        libraryViewModel.fetchContributors().observe(viewLifecycleOwner) { contributors ->
            contributorAdapter.swapData(contributors)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
