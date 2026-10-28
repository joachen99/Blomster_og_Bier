package in2000.gruppe8.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import in2000.gruppe8.R
import in2000.gruppe8.data.cache.JsonDataSource
import in2000.gruppe8.model.cache.ClimateNormalsCache
import in2000.gruppe8.model.cache.HeavyRainCache
import in2000.gruppe8.model.cache.ObservationCache
import in2000.gruppe8.model.cache.SourceCache
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    observationsDataSource: JsonDataSource<ObservationCache>,
    sourceDataSource: JsonDataSource<SourceCache>,
    heavyRainDataSource: JsonDataSource<HeavyRainCache>,
    climateNormalsDataSource: JsonDataSource<ClimateNormalsCache>,
    modifier: Modifier = Modifier
) {

    val coroutineScope = rememberCoroutineScope()
    val (cacheStatus, setCacheStatus) = remember { mutableStateOf<String?>(null) }

    // Language-selection
    val languageOptions = listOf(stringResource(R.string.language_norwegian),stringResource(R.string.language_english))
    val (selectedLanguage, onLanguageSelected) = remember { mutableStateOf(languageOptions[0]) }

    // Font-size selection
    val fontSizeOptions = when(selectedLanguage) {
        stringResource(R.string.language_norwegian) -> listOf(
            stringResource(R.string.font_small_no),
            stringResource(R.string.font_medium_no),
            stringResource(R.string.font_large_no)
        )
        else -> listOf(
            stringResource(R.string.font_small_en),
            stringResource(R.string.font_default_en),
            stringResource(R.string.font_large_en)
        )
    }
    val (selectedFontSize, onFontSizeSelected) = remember { mutableStateOf(fontSizeOptions[1]) }
    //TalkBack starts reading from the top of the screen:
    val headerFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        headerFocusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        val headerText = stringResource(R.string.settings_title)
        Text(
            text = headerText,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .semantics { heading() }
                .focusRequester(headerFocusRequester)
                .focusable()
                .semantics { traversalIndex = 0f }
        )

        // Language section
        val languageOptionText = stringResource(R.string.language_title)
        Text(
            text = languageOptionText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            languageOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (option == selectedLanguage),
                            onClick = { onLanguageSelected(option) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selectedLanguage),
                        onClick = null
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Font size section
        val fontOptionText = stringResource(R.string.font_size_title)
        Text(
            text = fontOptionText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            fontSizeOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (option == selectedFontSize),
                            onClick = { onFontSizeSelected(option) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selectedFontSize),
                        onClick = null
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // Simple preview with selected font size
        val previewFontSize = when (selectedFontSize) {
            stringResource(R.string.font_small_en),
            stringResource(R.string.font_small_no) -> 12.sp

            stringResource(R.string.font_large_en),
            stringResource(R.string.font_large_no) -> 20.sp

            else -> 16.sp
        }

        val previewText = stringResource(R.string.preview_font_text)
        Text(
            text = previewText,
            fontSize = previewFontSize,
            modifier = Modifier.padding(top = 16.dp)
        )

        //Clear cache
        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        val clearCacheText = stringResource(R.string.clear_cache)

        val cacheClearedText = stringResource(R.string.cache_cleared_successfully)
        val cacheClearFailedText = stringResource(R.string.cache_clear_failed)

        Button(
            onClick = {
                coroutineScope.launch {
                    val observationsDeleted = observationsDataSource.deleteCache()
                    val sourcesDeleted = sourceDataSource.deleteCache()
                    val heavyRainDeleted = heavyRainDataSource.deleteCache()
                    val climateNormalsDeleted = climateNormalsDataSource.deleteCache()

                    val allDeleted = observationsDeleted
                            && sourcesDeleted
                            && heavyRainDeleted
                            && climateNormalsDeleted

                    val message = if (allDeleted) {
                        cacheClearedText
                    } else {
                        cacheClearFailedText
                    }

                    setCacheStatus(message)
                }
            }
        ) {
            Text(clearCacheText)
        }
        //Cache deletion status
        cacheStatus?.let { status ->
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}