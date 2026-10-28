package in2000.gruppe8.ui.bee

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
import in2000.gruppe8.model.bees.BeeInfo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeeScreen(
    navController: NavController,
    beeViewModel: BeeViewModel
) {
    val beeUiState by beeViewModel.beeUiState.collectAsState()

    BeeGridScreen(
        cards = beeUiState.bee,
        navController = navController
    )
}

@Composable
fun BeeGridScreen(
    cards: List<BeeInfo>,
    navController: NavController
) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.heading_bees),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
            // Header text that scrolls together with the rest
            item {
                Text(
                    text = stringResource(R.string.beeScreen_description),
                    style = MaterialTheme.typography.titleMedium,
                    lineHeight = 28.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(items = cards, key = { bee -> bee.id }) { bee ->
                BeeCard(
                    bee,
                    onClick = { navController.navigate("bee_info/${bee.id}") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                    )
            }
        }
    }


private val imageMap = mapOf(
    "bee_friendly_plants" to R.drawable.bee_friendly_plants,
    "dandelion" to R.drawable.dandelion,
    "insect_hotel" to R.drawable.insect_hotel,
    "bee_shield" to R.drawable.bee_shield,
    "avoid_pesticides" to R.drawable.avoid_pesticides,
    "living_wall" to R.drawable.living_wall
)

@Composable
fun BeeCard(beeCard: BeeInfo, onClick: () -> Unit, modifier: Modifier = Modifier) {

    val imageResId = imageMap[beeCard.img]?: R.drawable.ic_biemedblomst

    Card(
        modifier = modifier
            .fillMaxWidth()
            // This should make it easier for someone who is visually impaired or blind to navigate the app:
            // More precise description of what the click does
            .clickable(
            onClickLabel = stringResource(
                R.string.action_click_pictureCard,
                beeCard.title
            ),
    onClick = onClick),

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
                contentDescription = null, // The image description appears if you press "more info" (in BeeInfoScreen)
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { heading() },
                    text = beeCard.title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
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
