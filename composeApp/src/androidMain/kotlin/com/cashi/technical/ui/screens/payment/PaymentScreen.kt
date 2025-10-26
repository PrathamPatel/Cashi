package com.cashi.technical.ui.screens.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cashi.technical.ui.components.CurrencyDropdown
import com.cashi.technical.ui.components.InputField
import com.cashi.technical.viewmodel.payment.PaymentsViewModel
import com.cashi.technical.viewmodel.payment.intents.PaymentIntent
import org.koin.compose.koinInject

/**
Created By: Pratham
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    viewModel: PaymentsViewModel = koinInject(),
    onNavigateToHistory : () -> Unit
){
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cashi Payment") },
                actions = {
                    TextButton(onClick = onNavigateToHistory) {
                        Text("History")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputField(
                value = state.email,
                onValueChange = { viewModel.handleIntent(PaymentIntent.ChangeEmail(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                label = "Recipient Email"
            )
            Spacer(Modifier.height(8.dp))
            InputField(
                value = state.amount,
                onValueChange = { viewModel.handleIntent(PaymentIntent.ChangeAmount(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                label = "Amount"
            )
            Spacer(Modifier.height(8.dp))
            CurrencyDropdown(
                selected = state.currency,
                onSelect = { viewModel.handleIntent(PaymentIntent.ChangeCurrency(it)) }
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.handleIntent(PaymentIntent.SendPayment) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Send Payment")
            }

            state.message?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}