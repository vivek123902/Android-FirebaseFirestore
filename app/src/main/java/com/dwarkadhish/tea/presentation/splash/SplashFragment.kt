package com.dwarkadhish.tea.presentation.splash

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dwarkadhish.tea.R
import com.dwarkadhish.tea.databinding.FragmentHomeBinding
import com.dwarkadhish.tea.databinding.FragmentSplashScreenBinding
import com.dwarkadhish.tea.presentation.base.BaseFragment


class SplashFragment : BaseFragment() {

    lateinit var binding: FragmentSplashScreenBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashScreenBinding.inflate(layoutInflater)
        baseBinding?.detailContainer?.addView(binding.root)
        baseBinding?.tvTitle?.visibility = View.GONE
        initAnimations()

        Handler().postDelayed({
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true) // Remove splashFragment from the backstack
                .build()
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment, null, navOptions)
        },2000)
    }

    private fun initAnimations() {
        val animLeftToRight = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left_to_right)
        val animRightToLeft = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right_to_left)
        val animBottomToTop = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_bottom_to_top)

        // Start animations simultaneously
        binding.ivTextview.startAnimation(animLeftToRight)
        binding.ivLogo.startAnimation(animRightToLeft)
    }

}