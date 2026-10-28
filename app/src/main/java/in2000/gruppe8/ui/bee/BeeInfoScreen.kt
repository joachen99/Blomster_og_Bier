package in2000.gruppe8.ui.bee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import in2000.gruppe8.R
import in2000.gruppe8.model.bees.BeeInfo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeeInfoScreen(
    beeViewModel: BeeViewModel,
    beeId: String,
    modifier: Modifier = Modifier
) {
    val beeUiState by beeViewModel.beeUiState.collectAsState()
    val chosenBee = beeUiState.bee
        .firstOrNull { bee ->
            bee.id == beeId
        }
    if (chosenBee != null) {
        MoreBeeInfo(
            bee = chosenBee,
            modifier = modifier
                .fillMaxWidth()
        )
    }
}

// Creates a list that maps images using an identifier
private val imageMap = mapOf(
    "bee_friendly_plants" to R.drawable.bee_friendly_plants,
    "dandelion" to R.drawable.dandelion,
    "insect_hotel" to R.drawable.insect_hotel,
    "bee_shield" to R.drawable.bee_shield,
    "avoid_pesticides" to R.drawable.avoid_pesticides,
    "living_wall" to R.drawable.living_wall
)

@Composable
fun MoreBeeInfo(bee: BeeInfo, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {

            Text(
                // The modifier with semantics makes it possible to navigate using headings
                // when a user is using TalkBack
                modifier = Modifier.semantics { heading() },
                text = bee.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            val imageResId = imageMap[bee.img]?: R.drawable.ic_biemedblomst


            AsyncImage(
                model = imageResId,
                contentDescription = stringResource(R.string.bee_photo, bee.title),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                // The modifier with semantics makes it possible to navigate using headings
                // when a user is using TalkBack
                modifier = Modifier.semantics { heading() },
                text = stringResource(R.string.heading_description),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = bee.description,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewBeeCard() {
    val mockAction = BeeInfo(
        id = "1",
        img = "",
        title = stringResource(R.string.destination_bees),
        description = stringResource(R.string.heading_description)
    )
    MoreBeeInfo(mockAction)
}