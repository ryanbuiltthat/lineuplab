package com.lineuplab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lineuplab.app.ui.navigation.LineupLabNavHost
import com.lineuplab.app.ui.theme.LineupLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LineupLabTheme {
                LineupLabNavHost()
            }
        }
    }
}
