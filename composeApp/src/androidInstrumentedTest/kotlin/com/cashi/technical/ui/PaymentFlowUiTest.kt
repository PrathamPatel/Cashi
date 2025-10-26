package com.cashi.technical.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.cashi.technical.MainActivity
import org.junit.Rule
import org.junit.Test

/**
Created By: Pratham
 */
class PaymentFlowUiTest {

    @get:Rule
    val composableTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testFullPaymentFlowToTransactionHistory(){
        composableTestRule.onNodeWithText("Send Payment").assertIsDisplayed()
        composableTestRule.onNodeWithText("Recipient Email").performTextInput("uiTest@cashi.com")
        composableTestRule.onNodeWithText("Amount").performTextInput("500.25")
        composableTestRule.onNodeWithText("Send Payment").performClick()

        composableTestRule.waitUntil(timeoutMillis = 5000) {
            composableTestRule.onAllNodesWithText("Payment sent!").fetchSemanticsNodes().isNotEmpty()
        }

        composableTestRule.onNodeWithText("History").performClick()

        composableTestRule.waitUntil(timeoutMillis = 5000){
            composableTestRule.onAllNodesWithText("500.25 USD").fetchSemanticsNodes().isNotEmpty()
        }

        composableTestRule.onNodeWithText("500.25 USD").assertIsDisplayed()
    }
}