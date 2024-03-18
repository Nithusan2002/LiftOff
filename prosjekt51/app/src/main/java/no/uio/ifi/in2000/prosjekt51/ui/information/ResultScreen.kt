package no.uio.ifi.in2000.prosjekt51.ui.information

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    latitude: String,
    longitude: String,
    date: Long,
    hour: Int,
    onNavigateToHomeScreen: () -> Unit,
    resultScreenViewModel: ResultScreenViewModel
) {
    val resultScreenUiState = resultScreenViewModel.uiState.collectAsState()

    val launchCheckResultText: String = resultScreenViewModel.checkLaunchConditions(lat = latitude.toDouble(), lon = longitude.toDouble(), date = date, hour = hour)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateToHomeScreen()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Column {
                Text(
                    "Resultat for ${latitude}° N ${longitude}° Ø",
                    fontSize = 20.sp
                )
                Text(
                    launchCheckResultText,
                    fontSize = 20.sp
                )
            }

            LazyColumn {

            }
        }
    }
}