package com.bsi.urban

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Specialmode(onFinish: () -> Unit) {
    Button(onClick = onFinish) { Text("Specialmode End") }
}