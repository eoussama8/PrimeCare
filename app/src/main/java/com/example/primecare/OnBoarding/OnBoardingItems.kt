package com.example.primecare.OnBoarding


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.primecare.R

data class OnBoardingItems(
    val image: Int,
    val icon: Int?,
    val title: Int,
    val description: Int
) {
    companion object {
        fun getData(): List<OnBoardingItems> {
            // Replace these with your actual resource IDs
            return listOf(
                OnBoardingItems(
                    R.drawable.into,
                    R.drawable.grid,
                    R.string.onBoardingText1,
                    R.string.onBoardingText1
                ),
                OnBoardingItems(
                    R.drawable.into,
                    R.drawable.grid,
                    R.string.onBoardingText1,
                    R.string.onBoardingText1
                ),
                OnBoardingItems(
                    R.drawable.into,
                    R.drawable.grid,
                    R.string.onBoardingText1,
                    R.string.onBoardingText1
                ),
                OnBoardingItems(
                    R.drawable.into,

                    R.drawable.grid,
                    R.string.onBoardingText1,
                    R.string.onBoardingText1
                )
            )
        }
    }
}