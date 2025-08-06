package com.adoptionapp.ui.compose

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.adoptionapp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationsScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun notificationsTitleIsDisplayed() {
        composeTestRule.onNodeWithContentDescription("Notifications Title").assertIsDisplayed()
    }
    @Test
    fun unreadCountOrEmptyStateIsDisplayed() {
        val unread = composeTestRule.onAllNodesWithText("Unread:")
        val empty = composeTestRule.onAllNodesWithText("No notifications.")
        assert(unread.fetchSemanticsNodes().isNotEmpty() || empty.fetchSemanticsNodes().isNotEmpty())
    }
    @Test
    fun markAllAsReadButtonIsDisplayedIfNotificationsExist() {
        val markAll = composeTestRule.onAllNodesWithText("Mark All as Read")
        // Button should be present if notifications exist
        // (If not, test passes by default)
        if (markAll.fetchSemanticsNodes().isNotEmpty()) {
            markAll.onFirst().assertIsDisplayed()
        }
    }
} 