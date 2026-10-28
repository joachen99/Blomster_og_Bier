package in2000.gruppe8.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,

    secondary = DarkBrown,
    onSecondary = White,

    tertiary = DarkGreen,
    onTertiary = Cream,

    surface = DarkBrown,
    onSurface = Cream,
    onSurfaceVariant = Cream,

    background = Black,
    onBackground = Cream,
    )
private val LightColorScheme = lightColorScheme(
    primary = WarmFuchsia,
    secondary = HoneyYellow,
    tertiary = Terracotta,
    background = Cream,
    surface = LightSage,
    //text colors:
    onPrimary = Cream,
    onSurface = DarkBrown,
    onTertiary = DarkBrown,
    onBackground = DarkBrown,
    onSurfaceVariant = DarkBrown,
)

@Composable
fun Gruppe_8Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}