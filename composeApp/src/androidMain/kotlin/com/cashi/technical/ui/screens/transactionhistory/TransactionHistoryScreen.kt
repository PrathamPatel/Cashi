package com.cashi.technical.ui.screens.transactionhistory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cashi.technical.model.Payment
import com.cashi.technical.ui.components.TransactionCard
import com.cashi.technical.ui.screens.payment.PaymentScreenContent
import com.cashi.technical.ui.screens.state.EmptyState
import com.cashi.technical.ui.screens.state.ErrorState
import com.cashi.technical.viewmodel.payment.state.PaymentUiState
import com.cashi.technical.viewmodel.transactionhistory.TransactionHistoryViewModel
import com.cashi.technical.viewmodel.transactionhistory.intents.TransactionIntent
import com.cashi.technical.viewmodel.transactionhistory.state.HistoryUiState
import org.koin.compose.koinInject

/**
Created By: Pratham
 */

//We don't create a wrapper for ViewModel as it is not necessary since Compose UI observes state flows
//Lifecycle handling is minimal.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    viewModel: TransactionHistoryViewModel = koinInject(),
    onBack : () -> Unit
){
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.handleIntent(TransactionIntent.LoadTransactions)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    ErrorState(
                        message = state.error ?: "Unknown error",
                        onRetry = {
                            viewModel.handleIntent(TransactionIntent.LoadTransactions)
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.payments.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        items(state.payments) { payment ->
                            TransactionCard(
                                recipientEmail = payment.recipientEmail,
                                amount = payment.amount,
                                currency = payment.currency,
                                timestamp = payment.timestamp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreenContent(state : HistoryUiState){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment History") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    ErrorState(
                        message = state.error ?: "Unknown error",
                        onRetry = {},
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.payments.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        items(state.payments) { payment ->
                            TransactionCard(
                                recipientEmail = payment.recipientEmail,
                                amount = payment.amount,
                                currency = payment.currency,
                                timestamp = payment.timestamp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionHistoryScreenPreview() {
    TransactionHistoryScreenContent(
        state = HistoryUiState(
            isLoading = false,
            payments = listOf(
                Payment(
                    recipientEmail = "preview@cashi.com",
                    amount = 100.toDouble(),
                    currency = "USD",
                    timestamp = System.currentTimeMillis()
                ),
                Payment(
                    recipientEmail = "preview2@cashi.com",
                    amount = 520.85,
                    currency = "EUR",
                    timestamp = System.currentTimeMillis() + 100000
                )
            ),
            error = null
        )
    )
}