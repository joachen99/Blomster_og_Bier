package in2000.gruppe8.ui.components


import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import in2000.gruppe8.R
import in2000.gruppe8.navigation.AppDestinations


// Navbar that takes navController as an argument
@Composable
fun BottomNavBar(navController: NavController, onMenuClick: () -> Unit) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    NavigationBar (

// Sets the navigation bar color to the custom secondary color in the theme
        containerColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .semantics {
            traversalIndex = 100f  // Make NavBar being read after all the other elements
        }

    ) {
        AppDestinations.entries.forEach { destination ->
            val semanticsLabel = stringResource(
                R.string.navbar_element_open,
                destination.label
            )
            NavigationBarItem(
                modifier = Modifier.semantics {
                    contentDescription = semanticsLabel
                },
                icon = {
                    val vectorIcon = destination.vectorIcon
                    val drawableIcon = destination.drawableIcon
                    when {
                        vectorIcon != null -> Icon(imageVector = vectorIcon, contentDescription = destination.label)
                        drawableIcon != null -> Icon(painter = painterResource(drawableIcon), contentDescription = destination.label)
                    }
                },
                label = { Text(text = destination.label, color = MaterialTheme.colorScheme.onSurface)},
                selected = currentRoute == destination.route,
                onClick = {
                    if (destination == AppDestinations.MENU) {
                        onMenuClick()
                    } else {
                        navController.navigate(destination.route) {
                            launchSingleTop = true
                        }
                    }
                }, colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) ,
                   // unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    indicatorColor =  MaterialTheme.colorScheme.tertiary,
                )
            )
        }
    }
}