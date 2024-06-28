package com.szabist.zabcafe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    // Define your dark color scheme here
)

private val LightColorScheme = lightColorScheme(
    // Define your light color scheme here
)

private val AppTypography = Typography(
    // Define your typography here
)

private val AppShapes = androidx.compose.material3.Shapes(
    // Define your shapes here
)

@Composable
fun ZABcafeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}