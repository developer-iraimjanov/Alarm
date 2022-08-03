package com.iraimjanov.alarm.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.iraimjanov.alarm.R
import com.iraimjanov.alarm.databinding.FragmentSplashBinding
import com.orhanobut.hawk.Hawk

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private val timer = object : CountDownTimer(2000, 100) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            replaceFragment()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        Hawk.init(requireActivity()).build()
        timer.start()

        return binding.root
    }

    private fun replaceFragment() {
        if (!Hawk.get("oneTime", true)) {
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_cautionFragment)
        }
    }

}