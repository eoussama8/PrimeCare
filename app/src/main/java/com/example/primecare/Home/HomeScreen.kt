package com.example.primecare.Home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Home Screen",
            fontSize = 24.sp,
            color = Color.White,

            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Welcome to PrimeCare!",
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )
    }
}