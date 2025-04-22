package com.example.primecare.OnBoarding


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.primecare.R

data class OnBoardingItems(
    @DrawableRes val image: Int,    // Background image
    @DrawableRes val icon: Int?,    // Icon for the card (nullable for fallback)
    @StringRes val title: Int,      // Title string resource
    @StringRes val description: Int // Description string resource
) {
    companion object {
        fun getData(): List<OnBoardingItems> {
            return listOf(
                OnBoardingItems(
                    image = R.drawable.into,
                    icon = R.drawable.book,  // Add your icon resource
                    title = R.string.onBoardingTitle1,
                    description = R.string.onBoardingText1
                ),
                OnBoardingItems(
                    image = R.drawable.into2,
                    icon = R.drawable.movie,  // Add your icon resource
                    title = R.string.onBoardingTitle2,
                    description = R.string.onBoardingText2
                ),
                OnBoardingItems(
                    image = R.drawable.into3,
                    icon = R.drawable.music,  // Add your icon resource
                    title = R.string.onBoardingTitle3,
                    description = R.string.onBoardingText3
                )
            )
        }
    }
}