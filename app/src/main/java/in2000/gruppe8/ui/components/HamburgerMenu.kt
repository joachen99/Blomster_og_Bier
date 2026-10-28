package in2000.gruppe8.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import in2000.gruppe8.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun HamburgerMenu(
    drawerState: DrawerState,
    navController: NavController,
) {
    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()


    val titleFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    //When opening hamburger menu, start reading from the top
    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            focusManager.clearFocus(force = true)
            titleFocusRequester.requestFocus()
        }
    }

    // Description of action (map) that can be read by a screen reader
    val mapDestinationName = stringResource(R.string.destination_map)
    val mapClickLabel = stringResource(
        R.string.hamburgermenu_items,
        mapDestinationName
    )

    // Description of action (climate) that can be read by a screen reader
    val climateDestinationName = stringResource(R.string.destination_climate_statistics)
    val climateClickLabel = stringResource(
        R.string.hamburgermenu_items,
        climateDestinationName
    )

    // Description of action (plants) that can be read by a screen reader
    val recommendedPlantsDestinationName = stringResource(R.string.destination_recommended_plants)
    val recommendedPlantsClickLabel = stringResource(
        R.string.hamburgermenu_items,
        recommendedPlantsDestinationName
    )

    // Description of action (bee measures) that can be read by a screen reader
    val beesDestinationName = stringResource(R.string.destination_bees)
    val beesClickLabel = stringResource(
        R.string.hamburgermenu_items,
        beesDestinationName
    )

    // Description of action (torrential rain measures) that can be read by a screen reader
    val actionsDestinationName = stringResource(R.string.destination_actions)
    val actionsClickLabel = stringResource(
        R.string.hamburgermenu_items,
        actionsDestinationName
    )

    // Description of action (about the app) that can be read by a screen reader
    val aboutDestinationName = stringResource(R.string.destination_about)
    val aboutClickLabel = stringResource(
        R.string.hamburgermenu_items,
        aboutDestinationName
    )

    // Description of action (settings) that can be read by a screen reader
    val settingsDestinationName = stringResource(R.string.destination_settings)
    val settingsClickLabel = stringResource(
        R.string.hamburgermenu_items,
        settingsDestinationName
    )

    // Description of action (close hamburger menu) that can be read by a screen reader
    val closeHamburgerMenuDestinationName = stringResource(R.string.destination_close_hamburgermenu)
    val closeHamburgerMenuClickLabel = stringResource(
        R.string.hamburgermenu_items,
        closeHamburgerMenuDestinationName
    )

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.75f),  // Takes up 75% of the screen
        // Changes the background color of the drawer menu
        drawerContainerColor = MaterialTheme.colorScheme.background
    ) {
            Column(modifier = Modifier
            //.weight(1f)
                .padding(16.dp)
                .verticalScroll(scrollState) //Makes only Hamburger menu Scrollable
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = stringResource(R.string.menu),
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 16.dp)
                        .semantics { heading() }
                        .focusRequester(titleFocusRequester)
                        .focusable()
                )
                // Close button in the top right corner
                IconButton(
                    modifier = Modifier.padding(7.dp).semantics {
                        onClick(
                            label = closeHamburgerMenuClickLabel,
                            action = null
                        )
                    },
                    onClick = { scope.launch { drawerState.close() } }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_menu),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Each NavigationDrawerItem contains a descriptive title and navigates to the relevant screen
            // To add more items, copy the structure and place it where desired below


            // Map
            NavigationDrawerItem(
                // Describes what this click does for the screen reader
                modifier = Modifier.semantics {
                    onClick(
                        label = mapClickLabel,
                        action = null
                    )
                },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PinDrop,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(text = stringResource(R.string.menu_choose_location))
                    }
                },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("map")
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
            // Climate statistics
            NavigationDrawerItem(
                modifier = Modifier.semantics {
                    onClick(
                        label = climateClickLabel,
                        action = null
                    )
                },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_monitoring),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(text = stringResource(R.string.menu_your_climate))
                    }
                },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("climate_statistics")
                }
            )
            // Recommended plants
            NavigationDrawerItem(
                modifier = Modifier.semantics {
                    onClick(
                        label = recommendedPlantsClickLabel,
                        action = null
                    )
                },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFlorist,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = stringResource(R.string.destination_recommended_plants))
                    }
                },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(route = "recommended_plants")
                }
            )

            // Recommended bee measures
            NavigationDrawerItem(
                modifier = Modifier.semantics {
                    onClick(
                        label = beesClickLabel,
                        action = null
                    )
                },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bie),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = stringResource(R.string.destination_bees))
                    }
                },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("bees")
                }
            )

            // Recommended torrential rain measures
            NavigationDrawerItem(
                modifier = Modifier.semantics {
                    onClick(
                        label = actionsClickLabel,
                        action = null
                    )
                },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_rain),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = stringResource(R.string.destination_actions))
                    }
                },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("actions")
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
            // About the app
            NavigationDrawerItem(
                modifier = Modifier.semantics {
                    onClick(
                        label = aboutClickLabel,
                        action = null
                    )
                },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(text = stringResource(R.string.destination_about))
                    }
                },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("about_us")
                }
            )

            // Settings
            NavigationDrawerItem(
                modifier = Modifier.semantics {
                    onClick(
                        label = settingsClickLabel,
                        action = null
                    )
                },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(text = stringResource(R.string.destination_settings))
                    }
                },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("settings")
                }
            )
        } // Bee icon logo in the bottom
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Shows the app icon in the hamburger menu dynamically, so it adapts to the screen size
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
            ) {
                // Max 70% of the width, but never more than 200 dp
                val iconSize = min(maxWidth * 0.70f, 200.dp)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bee_icon_without_background),
                        contentDescription = stringResource(R.string.app_icon_description),
                        modifier = Modifier
                            .size(iconSize)
                            .padding(start = 3.dp),
                    )
                    Text(
                        text = "Blomster & Bier\n© 2026",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .padding(start = 3.dp)
                            .weight(1f),
                        maxLines = 3,
                        softWrap = true
                        )
                    }
                }
        }
    }
}