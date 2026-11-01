package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsportsDarkColorScheme =
  darkColorScheme(
    primary = EsportsRed,
    onPrimary = EsportsTextWhite,
    primaryContainer = EsportsRedDark,
    onPrimaryContainer = EsportsTextWhite,
    secondary = EsportsBlue,
    onSecondary = EsportsBlack,
    secondaryContainer = EsportsBlueDark,
    onSecondaryContainer = EsportsTextWhite,
    background = EsportsBlack,
    onBackground = EsportsTextWhite,
    surface = EsportsDarkSurface,
    onSurface = EsportsTextWhite,
    surfaceVariant = EsportsCardSurface,
    onSurfaceVariant = EsportsTextGray,
    error = EsportsRed,
    onError = EsportsTextWhite
  )

private val EsportsLightColorScheme =
  lightColorScheme(
    primary = EsportsRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCDD2),
    onPrimaryContainer = Color(0xFFB71C1C),
    secondary = EsportsBlueDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF006064),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1A1A1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1E),
    surfaceVariant = Color(0xFFECEFF1),
    onSurfaceVariant = Color(0xFF546E7A),
    error = EsportsRed,
    onError = Color.White
  )

@Composable
fun OgWarriorsTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) EsportsDarkColorScheme else EsportsLightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
