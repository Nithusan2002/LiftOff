package no.uio.ifi.in2000.prosjekt51

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import no.uio.ifi.in2000.prosjekt51.ui.map.MapScreen
import no.uio.ifi.in2000.prosjekt51.ui.map.MapViewModel
import no.uio.ifi.in2000.prosjekt51.ui.result.VisualResultScreen
import no.uio.ifi.in2000.prosjekt51.ui.result.ResultScreenViewModel
import org.junit.Test
import org.junit.Rule

class VisualResultScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun visualResultScreen_DisplayStateChangesCorrectly() {
        // Mock data and ViewModel setup
        val viewModel = ResultScreenViewModel()

        composeTestRule.setContent {
            VisualResultScreen(
                latitude = "59.91",
                longitude = "10.75",
                date = System.currentTimeMillis(),
                hour = "12",
                height = 100.0,
                resultScreenViewModel = viewModel,
                onNavigateToHomeScreen = {},
                navController = rememberNavController(),
                snackbarHostState = SnackbarHostState(),
                onRetryClicked = {},
                errorMessage = null,
                onNavigateToResultScreen = { latitude: String, longitude: String, date: Long, hour: String ->
                    { }
                }
            )
        }

        val nodesWithSight = composeTestRule.onAllNodesWithText("Sight")
        if (nodesWithSight.fetchSemanticsNodes().size > 1) {
            nodesWithSight[1].performClick()
        } else {
            throw IllegalStateException("Expected more than one node with 'Sight' but found ${nodesWithSight.fetchSemanticsNodes().size}")
        }

        composeTestRule.onNodeWithTag("UV").assertIsDisplayed()
    }
}


class MapScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mapScreen_ButtonsAppearWhenMapClicked() {
        val testViewModel = MapViewModel()

        composeTestRule.setContent {
            MapScreen(navController = rememberNavController(), viewModel = testViewModel)
        }

        composeTestRule.runOnUiThread {
            testViewModel.selectLocation(LatLng(59.911491, 10.757933))
            testViewModel.toggleSaveDialog(true)
        }

        composeTestRule.onNodeWithText("Save position to favourites").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search position").assertIsDisplayed()
    }

    @Test
    fun mapScreen_SaveToFavouritesDialogueAppears() {
        val testViewModel = MapViewModel()

        composeTestRule.setContent {
            MapScreen(navController = rememberNavController(), viewModel = testViewModel)
        }

        // Simulate map location selection
        composeTestRule.runOnUiThread {
            testViewModel.selectLocation(LatLng(59.911491, 10.757933))
        }

        // Simulate button click to open save dialog
        composeTestRule.onNodeWithText("Save position to favourites").performClick()

        // Check that the dialog and the input field appear
        composeTestRule.onNodeWithText("Save to Favorites").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter a name for the location").assertIsDisplayed()
    }
}