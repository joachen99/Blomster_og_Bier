package in2000.gruppe8.ui.actions

import android.content.Intent
import android.text.style.URLSpan
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import in2000.gruppe8.R
import in2000.gruppe8.model.actions.ActionInfo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionsInfoScreen(
    actionsViewModel: ActionsViewModel,
    actionId: String,
    modifier: Modifier = Modifier
) {
    val actionsUiState by actionsViewModel.actionsUiState.collectAsState()
    val chosenAction = actionsUiState.action
        .firstOrNull { action ->
            action.id == actionId
        }
    if (chosenAction != null) {
        MoreActionInfo(
            action = chosenAction,
            modifier = modifier
                .fillMaxWidth()
        )
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
fun MoreActionInfo(action: ActionInfo, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier
            .fillMaxSize()
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
                text = action.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            val imageResId = imageMap[action.img]?: R.drawable.ic_biemedblomst


            AsyncImage(
                model = imageResId,
                contentDescription = stringResource(R.string.action_photo, action.title),
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
                modifier = Modifier
                    .padding(8.dp)
                    .semantics { heading() },
                text = stringResource(R.string.heading_description),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = action.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                // The modifier with semantics makes it possible to navigate using headings
                // when a user is using TalkBack
                modifier = Modifier
                    .padding(8.dp)
                    .semantics { heading() },
                text = stringResource(R.string.heading_procedure),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground

            )

            HtmlText(
                html = action.procedure,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// To create a clickable link, and keep the same font throughout the body text
@Composable
fun HtmlText(
    html: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    val context = LocalContext.current
    val linkColor = MaterialTheme.colorScheme.primary


    val spanned = remember(html) {
        HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    val annotated = remember(spanned, linkColor) {
        buildAnnotatedString {
            append(spanned.toString())

            val urlSpans = spanned.getSpans(0, spanned.length, URLSpan::class.java)
            urlSpans.forEach { span ->
                val start = spanned.getSpanStart(span)
                val end = spanned.getSpanEnd(span)

                addStyle(
                    SpanStyle(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline
                    ),
                    start,
                    end
                )

                addStringAnnotation(
                    tag = "URL",
                    annotation = span.url,
                    start = start,
                    end = end
                )
            }
        }
    }

    // Clickable text (deprecated, but works in ALL Compose versions)
    // Tried switching to a newer version, but it does not work with the Compose version we use
    ClickableText(
        text = annotated,
        style = style.copy(color = color),
        onClick = { offset ->
            annotated.getStringAnnotations("URL", offset, offset)
                .firstOrNull()
                ?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, annotation.item.toUri())
                    context.startActivity(intent)
                }
        }
    )
}




@Preview(showBackground = true)
@Composable
fun PreviewActionCard() {
    val mockAction = ActionInfo(
        id = "1",
        img = "",
        title = stringResource(R.string.destination_actions),
        description = stringResource(R.string.heading_description),
        procedure = stringResource(R.string.heading_procedure)
    )
    MoreActionInfo(mockAction)
}