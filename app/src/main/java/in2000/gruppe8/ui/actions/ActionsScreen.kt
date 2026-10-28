package in2000.gruppe8.ui.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import in2000.gruppe8.R
import in2000.gruppe8.model.actions.ActionInfo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionsScreen(
    navController: NavController,
    actionsViewModel: ActionsViewModel,
) {
    val actionsUiState by actionsViewModel.actionsUiState.collectAsState()

    ActionsGridScreen(
        cards = actionsUiState.action,
        navController = navController
    )
}

@Composable
fun ActionsGridScreen(
    cards: List<ActionInfo>,
    navController: NavController,
) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.heading_torrential),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
            // Header text that scrolls together with the rest
            item {
                Text(
                    text = stringResource(R.string.actionsScreen_description),
                    style = MaterialTheme.typography.titleMedium,
                    lineHeight = 28.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                    )
            }


            // The cards
            items(items = cards, key = { action -> action.id }) { action ->
                ActionCard(
                    action,
                    onClick = { navController.navigate("action_info/${action.id}") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                    )
            }
        }
    }


private val imageMap = mapOf(
    "direct_the_water" to R.drawable.direct_the_water,
    "vegetation" to R.drawable.vegetation,
    "flower_ditch" to R.drawable.flower_ditch,
    "rain_barrel" to R.drawable.rain_barrel,
    "_12019_north_pole_1632525" to R.drawable._12019_north_pole_1632525,
    "flood_dam" to R.drawable.flood_dam
)

@Composable
fun ActionCard(actionCard: ActionInfo, onClick: () -> Unit, modifier: Modifier = Modifier) {
    
    val imageResId = imageMap[actionCard.img]?: R.drawable.ic_biemedblomst

    Card(
        modifier = modifier
            .fillMaxWidth()
            // This should make it easier for someone who is visually impaired or blind to navigate the app:
            // More precise description of what the click does
            .clickable(
                onClickLabel = stringResource(
                    R.string.action_click_pictureCard,
                    actionCard.title
            ),
        onClick = onClick,
                ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)

    ) {
            Column (modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally

            ){
                AsyncImage(
                    model = imageResId,
                    contentDescription = null, // The image description appears if you press "more info" (in ActionsInfoScreen)
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )


                // The modifier with semantics makes it possible to navigate using headings
                // when a user is using a screen reader

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { heading() },
                        text = actionCard.title,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterEnd),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
    }
}
