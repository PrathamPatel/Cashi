package com.cashi.technical.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cashi.technical.ui.screens.payment.PaymentScreen
import com.cashi.technical.ui.screens.transactionhistory.TransactionHistoryScreen

/**
Created By: Pratham
 */

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "payment"
    ){
        composable("payment"){
            PaymentScreen(
                onNavigateToHistory = {
                    navController.navigate("history")
                }
            )
        }

        composable("history") {
            TransactionHistoryScreen(onBack = {
                navController.popBackStack()
            })
        }
    }
}