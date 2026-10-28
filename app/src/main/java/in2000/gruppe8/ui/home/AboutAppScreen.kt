package in2000.gruppe8.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import in2000.gruppe8.R

@Composable
fun AboutAppScreen() {
    val headerFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        headerFocusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            // The modifier with semantics makes it possible to navigate using headings
            modifier = Modifier.semantics { heading() },
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium

        )

        Text(
            text = stringResource(R.string.about_app_intro),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            // The modifier with semantics makes it possible to navigate using headings
            modifier = Modifier.semantics { heading() },
            text = stringResource(R.string.how_it_works_title),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(R.string.how_it_works_text),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            // The modifier with semantics makes it possible to navigate using headings
            modifier = Modifier.semantics { heading() },
            text = stringResource(R.string.what_you_get_title),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(R.string.bullet_plants),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = stringResource(R.string.bullet_actions),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = stringResource(R.string.bullet_climate),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)

        Text(
            text = stringResource(R.string.version) ,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(R.string.developers_title),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(R.string.developers_names),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = stringResource(R.string.thanks_title),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(R.string.supervisors_names),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}