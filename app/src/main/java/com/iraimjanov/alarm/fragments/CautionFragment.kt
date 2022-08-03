package com.iraimjanov.alarm.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.iraimjanov.alarm.R
import com.iraimjanov.alarm.databinding.FragmentCautionBinding
import com.orhanobut.hawk.Hawk

class CautionFragment : Fragment() {
    private lateinit var binding: FragmentCautionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCautionBinding.inflate(layoutInflater)
        Hawk.init(requireActivity()).build()

        showFragment()

        binding.overlayAllow.setOnClickListener {
            getTopWindowsPermission()
        }

        binding.appSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + requireActivity().packageName)
            startActivity(intent)
        }

        binding.next.setOnClickListener {
            Hawk.put("oneTime", false)
            findNavController().navigate(R.id.action_cautionFragment_to_homeFragment)
        }


        return binding.root
    }

    private fun showFragment() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            binding.cardTopWindowPermission.visibility = View.GONE
        }
    }

    private fun getTopWindowsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(requireActivity())) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${requireActivity().packageName}")
                )
                startActivity(intent)
            }
        }
    }

}