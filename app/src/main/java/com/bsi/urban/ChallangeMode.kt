package com.bsi.urban

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Challangemode(onFinish: () -> Unit) {
    Button(onClick = onFinish) { Text("Challangemode End") }
}