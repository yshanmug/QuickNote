package com.yuva.notetakingapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MediumSeaGreen,
    onPrimary = Black,
    secondary = MintGreen,
    onSecondary = Black,
    onSecondaryContainer = Black, // Description text
    background = White,
    surfaceVariant = DarkGrey, //border
    tertiary =  MediumDarkGrey, //delete icon
    surface = MediumSeaGreen, //fab button
    onSurface = Black, //+ icon
    onTertiary = MediumDarkGrey,
)


private val DarkColorScheme = darkColorScheme(
    primary = DarkBlack,
    onPrimary = DarkGreen,
    secondary = LightBlack,
    onSecondary = LightWhite,
    background = DarkBlack,
    onSecondaryContainer = VeryLightGrey ,
    surfaceVariant = LightWhite,  //border
    tertiary = MediumDarkGrey, //delete icon
    onTertiary = LightWhite,
    surface = DarkGreen,// fab button
    onSurface = Black, //+ icon
)


@Composable
fun NoteTakingAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}