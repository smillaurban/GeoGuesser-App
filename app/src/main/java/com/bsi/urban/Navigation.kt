package com.bsi.urban

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text



@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "navigation") {
            composable("navigation") {
                Navigation(navController) }

            composable("singleplayer") {
                Singleplayer(onFinish = {
                navController.navigate("endscreen")
            })}

            composable("multiplayer") {
                Multiplayer(onFinish = {
                navController.navigate("endscreen")
            })}

            composable("challangemode") {
                Challangemode(onFinish = {
                navController.navigate("endscreen")
            })}

            composable("specialmode") {
                Specialmode(onFinish = {
                navController.navigate("endscreen")
            })}

            composable("endscreen") {
                EndScreen(onFinish = {
                navController.navigate("endscreen")
            })}
    }
}

@Composable
fun Navigation(navController: NavController) {
    Column {
        Button(onClick = { navController.navigate("singleplayer") }) {
            Text("Singleplayer")
        }
        Button(onClick = { navController.navigate("multiplayer") }) {
            Text("Multiplayer")
        }
        Button(onClick = { navController.navigate("challangemode") }) {
            Text("Challangemode")
        }
        Button(onClick = { navController.navigate("specialmode") }) {
            Text("Specialmode")
        }
    }
}



