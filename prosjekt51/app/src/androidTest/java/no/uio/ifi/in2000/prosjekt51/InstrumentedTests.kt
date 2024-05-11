package no.uio.ifi.in2000.prosjekt51

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.prosjekt51.ui.theme.ThemeManager
import com.google.android.gms.maps.model.LatLng
import no.uio.ifi.in2000.prosjekt51.ui.favorites.FavoritesListScreen
import no.uio.ifi.in2000.prosjekt51.ui.map.MapScreen
import no.uio.ifi.in2000.prosjekt51.ui.map.MapViewModel
import no.uio.ifi.in2000.prosjekt51.ui.result.VisualResultScreen
import no.uio.ifi.in2000.prosjekt51.ui.result.ResultScreenViewModel
import no.uio.ifi.in2000.prosjekt51.ui.settings.SettingsScreen
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith

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
                onNavigateToResultScreen = { _: String, _: String, _: Long, _: String ->
                    { }
                }
            )
        }

        val nodesWithSight = composeTestRule.onAllNodesWithText("Show more...")
        if (nodesWithSight.fetchSemanticsNodes().size > 1) {
            nodesWithSight[1].performClick()
        } else {
            throw IllegalStateException("Expected more than one node with 'Sight' but found ${nodesWithSight.fetchSemanticsNodes().size}")
        }

        composeTestRule.onNodeWithTag("UV").assertIsDisplayed()

    }
}


class FavoritesTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mapScreen_ButtonsAppearWhenMapClicked() {

        composeTestRule.setContent {
            FavoritesListScreen(navController = rememberNavController())
        }

        composeTestRule.onNodeWithTag("addFavoriteButton").performClick()
        composeTestRule.onNodeWithText("Add New Favorite").assertIsDisplayed()
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

        composeTestRule.onNodeWithText("Save position to favorites").assertIsDisplayed()
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
        composeTestRule.onNodeWithText("Save position to favorites").performClick()

        // Check that the dialog and the input field appear
        composeTestRule.onNodeWithText("Save to Favorites").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter a name for the location").assertIsDisplayed()
    }
}

class ThemeSwitchingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun toggleTheme_changesThemeAccordingly() {
        // Set the initial content for the test
        composeTestRule.setContent {
            SettingsScreen()
        }

        // Wait until the UI is stable
        composeTestRule.waitForIdle()

        // Determine if the "Dark" or "Light" theme is initially active by checking which node exists
        val isInitiallyDark = try {
            composeTestRule.onNodeWithText("Appearance: Dark").assertExists()
            true
        } catch (e: AssertionError) {
            false
        }

        val initialStateText = if (isInitiallyDark) "Appearance: Dark" else "Appearance: Light"
        val expectedNewStateText = if (isInitiallyDark) "Appearance: Light" else "Appearance: Dark"

        // Perform a click to toggle the theme
        composeTestRule.onNodeWithText(initialStateText).performClick()

        // Wait for the UI to process the click and update
        composeTestRule.waitForIdle()

        // Check that the appearance has toggled from its initial state
        composeTestRule.onNodeWithText(expectedNewStateText).assertIsDisplayed()

        // Optionally toggle back and verify the change back to the initial state
        composeTestRule.onNodeWithText(expectedNewStateText).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(initialStateText).assertIsDisplayed()
    }
}
