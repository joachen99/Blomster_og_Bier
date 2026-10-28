package in2000.gruppe8.ui.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import in2000.gruppe8.R

@Composable
fun AddressSearchBar(
    query: String,
    results: List<Address>,
    isLoading: Boolean,
    onQueryChanged: (String) -> Unit,
    onAddressSelected: (Address) -> Unit,
    onClearResults: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)

    ) {
        // Search field
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            placeholder = { Text(stringResource(R.string.map_search_placeholder)) },
            leadingIcon = {
                if (isLoading)
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else
                    Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearResults) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.map_search_clear))
                    }
                }
            },
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colorScheme.background,
                unfocusedContainerColor = colorScheme.background,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(28.dp)),
            singleLine = true
        )

        // Result list
        if (results.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 3.dp,
                color = colorScheme.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
            ) {
                LazyColumn {
                    items(results) { address ->
                        ListItem(
                            headlineContent = { Text(address.addressText) },
                            supportingContent = {
                                if (!address.postPlace.isNullOrBlank()) {
                                    Text("${address.postNumber ?: ""} ${address.postPlace}".trim())
                                }
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            ),
                            modifier = Modifier.clickable {
                                keyboardController?.hide()
                                onClearResults()
                                onAddressSelected(address)
                            }
                        )
                        HorizontalDivider(color = colorScheme.onSurface)
                    }
                }
            }
        }
    }
}