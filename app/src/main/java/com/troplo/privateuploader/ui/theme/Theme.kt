package com.troplo.privateuploader.ui.theme

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowCompat
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.ThemeOption
import kotlinx.coroutines.flow.MutableStateFlow

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    background = Color(0xFF101010),
    error = Red
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    error = Red
)

@Composable
fun PrivateUploaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
    selected: MutableStateFlow<ThemeOption> = SessionManager(LocalContext.current).theme,
) {
    var isDark = isSystemInDarkTheme()
    val select = selected.collectAsState()
    val accent = SessionManager(LocalContext.current).getColor()
    if (select.value == ThemeOption.Dark || select.value == ThemeOption.AMOLED) {
        isDark = true
    } else if (select.value == ThemeOption.Light) {
        isDark = false
    }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            Log.d("Theme", "Accent: $accent, Selected: $select")
            if (accent != null) {
                try {
                    dynamicDarkColorScheme(context).copy(
                        primary = Color(accent.toColorInt())
                    )
                } catch (e: Exception) {
                    //
                }
            }

            if (isDark) {
                if (select.value == ThemeOption.AMOLED) {
                    dynamicDarkColorScheme(context).copy(
                        background = Color(0xFF000000),
                        surface = Color(0xFF000000),
                        surfaceVariant = Color(0xFF0F1015),
                        error = Red
                    )
                } else {
                    dynamicDarkColorScheme(context)
                }
            } else dynamicLightColorScheme(context)
        }

        isDark -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}