package com.cashi.technical

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.cashi.technical.navigation.AppNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview(showBackground = true, name = "App Navigation")
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}