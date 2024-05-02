package no.uio.ifi.in2000.prosjekt51

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.prosjekt51.ui.result.VisualResultScreen
import no.uio.ifi.in2000.prosjekt51.ui.result.VisualResultScreenViewModel
import org.junit.Test
import org.junit.Rule

class VisualResultScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun visualResultScreen_DisplayStateChangesCorrectly() {
        // Mock data and ViewModel setup
        val viewModel = VisualResultScreenViewModel()

        composeTestRule.setContent {
            VisualResultScreen(
                latitude = "59.91",
                longitude = "10.75",
                date = System.currentTimeMillis(),
                hour = 12,
                height = 100.0,
                visualResultScreenViewModel = viewModel,
                onNavigateToHomeScreen = {},
                navController = rememberNavController(),
                snackbarHostState = SnackbarHostState(),
                onRetryClicked = {},
                errorMessage = null
            )
        }

        val nodesWithWind = composeTestRule.onAllNodesWithText("Sight")
        if (nodesWithWind.fetchSemanticsNodes().size > 1) {
            // Assuming the second node is the correct one to interact with
            nodesWithWind[1].performClick()
        } else {
            throw IllegalStateException("Expected more than one node with 'Wind' but found ${nodesWithWind.fetchSemanticsNodes().size}")
        }

        composeTestRule.onNodeWithTag("UV").assertIsDisplayed()
    }
}
