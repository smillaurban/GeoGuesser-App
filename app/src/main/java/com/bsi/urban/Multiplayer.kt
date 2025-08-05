package com.bsi.urban

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text

@Composable
fun Multiplayer(onFinish: () -> Unit) {
    Button(onClick = onFinish) { Text("Multiplayer End") }
}