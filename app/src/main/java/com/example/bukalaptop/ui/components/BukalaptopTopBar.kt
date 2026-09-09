package com.example.bukalaptop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bukalaptop.R

@Composable
fun BukalaptopTopBar(modifier: Modifier = Modifier) {
    // Top bar
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(Color(0xFFEDEDED)), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logs),
            contentDescription = null,
            modifier = Modifier.height(60.dp)
        )
    }
}