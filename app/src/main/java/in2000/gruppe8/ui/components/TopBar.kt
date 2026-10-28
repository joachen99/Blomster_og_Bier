package in2000.gruppe8.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import in2000.gruppe8.R


// Top bar gets its title depending on the label from the AppDestinations enum class (the logic is written in MainActivity)
// Top bar has an optional back button (needed for ActionsInfoScreen)
@ExperimentalMaterial3Api
@Composable
fun TopBar(
    title: String,
    showBack: Boolean,
    onBackClick: () -> Unit,
    logoRes: Int? = null
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.shadow(4.dp),
        title = {
            Text(
                // The modifier with semantics makes the headings actually function as headings
                // when a user is using a screen reader
                modifier = Modifier
                    .semantics { heading() },
                text = title,
                fontFamily = FontFamily(Font(R.font.bree_regular)),
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.button_navigate_up),
                        tint = MaterialTheme.colorScheme.primary

                    )
                }
            } else if (logoRes != null) {
                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    )
}


// Lets us decide the title for each screen, route is sent from the AppDestinations enum class
@Composable
fun getTitle(route: String?): String {
    return when (route) {
        "actions" -> stringResource(R.string.destination_actions)
        "bees" -> stringResource(R.string.destination_bees)
        "recommended_plants" -> stringResource(R.string.destination_recommended_plants)
        "home" -> stringResource(R.string.app_name)
        "map" -> stringResource(R.string.menu_choose_location)
        "settings" -> stringResource(R.string.destination_settings)
        "climate_statistics" -> stringResource(R.string.menu_your_climate)
        "about_us" -> stringResource(R.string.destination_about)
        else -> ""
    }
}


@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TopBar(stringResource(R.string.app_name), showBack = false, onBackClick = {})
}