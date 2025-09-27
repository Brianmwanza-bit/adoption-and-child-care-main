package com.adoptionapp.ui.compose

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.adoptionapp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnalyticsScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun analyticsTitleIsDisplayed() {
        composeTestRule.onNodeWithContentDescription("Analytics Title").assertIsDisplayed()
    }
    @Test
    fun summaryOrRetryIsDisplayed() {
        // Try to find summary or retry button (if error)
        val summary = composeTestRule.onAllNodesWithText("Summary:")
        val retry = composeTestRule.onAllNodesWithText("Retry")
        assert(summary.fetchSemanticsNodes().isNotEmpty() || retry.fetchSemanticsNodes().isNotEmpty())
    }
} 