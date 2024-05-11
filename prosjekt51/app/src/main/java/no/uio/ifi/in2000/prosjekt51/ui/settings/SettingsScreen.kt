package no.uio.ifi.in2000.prosjekt51.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.prosjekt51.ui.theme.ThemeManager

@ExperimentalMaterial3Api
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val isDarkTheme = ThemeManager.getThemeState(context)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Settings") })
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            SettingsItem(
                onClick = {
                    isDarkTheme.value = !isDarkTheme.value
                    ThemeManager.saveTheme(context, isDarkTheme.value)
                },
                name = "Appearance: ${if (isDarkTheme.value) "Dark" else "Light"}",
                modifier = Modifier.testTag("ThemeToggle")
            )
            SettingsItem(onClick = {}, name = "Units and boundary values" )
            SettingsItem(onClick = {}, name = "Restore defaults" )
        }
    }
}


@Composable
fun SettingsItem(onClick: () -> Unit, name: String, modifier: Modifier = Modifier) {
    Card(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
