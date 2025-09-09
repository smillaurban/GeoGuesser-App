package com.bsi.urban

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bsi.urban.ui.theme.UrbanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UrbanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainMenu(
                        onSingleplayerClick = {
                            startActivity(Intent(this, SinglePlayer::class.java))
                        },
                        onMultiplayerClick = {
                            startActivity(Intent(this, MultiplayerActivity::class.java))
                        },
                        onChallengeClick = {
                            startActivity(Intent(this, ChallengeActivity::class.java))
                        },
                        onCountryModeClick = {
                            startActivity(Intent(this, CountryModeActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainMenu(
    onSingleplayerClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onChallengeClick: () -> Unit,
    onCountryModeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onSingleplayerClick,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) { Text("Einzelspieler") }

        Button(
            onClick = onMultiplayerClick,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) { Text("Multiplayer") }

        Button(
            onClick = onChallengeClick,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) { Text("Challenge-Modus") }

        Button(
            onClick = onCountryModeClick,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Länder/Städte-Modus") }
    }
}
