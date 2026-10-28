package in2000.gruppe8.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.ui.graphics.vector.ImageVector
import in2000.gruppe8.R

// Enum class for bottomNavBar and topBar
enum class AppDestinations(
    val label: String,
    val drawableIcon: Int? = null,
    val vectorIcon: ImageVector? = null,
    val route: String
) {
    MENU(
        label = "Meny",
        drawableIcon = R.drawable.ic_menu,
        route = "menu"
    ),

    CLIMATE(
        label = "Klima",
        drawableIcon = R.drawable.ic_monitoring,
        route = "climate_statistics"
    ),

    PLANTS(
        label = "Planter",
        vectorIcon = Icons.Default.LocalFlorist,
        route = "recommended_plants"
    ),

    BEES(
        label = "Bier",
        drawableIcon = R.drawable.ic_bie,
        route = "bees"
    ),

    RECOMMENDED_ACTIONS(
        label = "Styrtregn",
        drawableIcon = R.drawable.ic_rain,
        route = "actions"
    ),
}