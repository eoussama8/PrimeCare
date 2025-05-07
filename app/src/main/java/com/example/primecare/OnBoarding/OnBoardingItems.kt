package com.example.primecare.OnBoarding


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.primecare.R

// Model class for onboarding items
data class OnBoardingItems(
    val image: Int,
    val title: Int,
    val description: Int
) {
    companion object {
        fun getData(): List<OnBoardingItems> {
            return listOf(
                OnBoardingItems(
                    R.drawable.onboarding_1,
                    R.string.onboarding_title_1,
                    R.string.onboarding_desc_1
                ),
                OnBoardingItems(
                    R.drawable.onboarding_2,
                    R.string.onboarding_title_2,
                    R.string.onboarding_desc_2
                ),
                OnBoardingItems(
                    R.drawable.onboarding_3,
                    R.string.onboarding_title_3,
                    R.string.onboarding_desc_3
                )
            )
        }
    }
}