package com.cashi.technical.viewmodel.transactionhistory

import com.cashi.technical.di.provider.DispatcherProvider
import com.cashi.technical.repository.PaymentsRepository
import com.cashi.technical.util.NetworkMonitor
import com.cashi.technical.viewmodel.transactionhistory.intents.TransactionIntent
import com.cashi.technical.viewmodel.transactionhistory.state.HistoryUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
Created By: Pratham
 */
//We do not extend ViewModel() because this compiles to multiple targets (In real world scenario) and ViewModel
//is from androidx.lifecylce.viewModel which is ONLY for Android.
class TransactionHistoryViewModel(
    private val paymentsRepository: PaymentsRepository,
    private val dispatcher : DispatcherProvider,
    private val networkMonitor: NetworkMonitor
) {
    private val scope = CoroutineScope(dispatcher.io)

    private val _state = MutableStateFlow(HistoryUiState())
    val state = _state.asStateFlow()

    init {
        observeTransactions()
    }
    fun handleIntent(intent : TransactionIntent){
        when(intent){
            TransactionIntent.LoadTransactions-> observeTransactions()
        }
    }

    private fun observeTransactions() {
        if(!networkMonitor.isInternetAvailable()){
            _state.update { it.copy(error = "Internet connection required to view transaction history") }
        }
        else{
            scope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                try {
                    paymentsRepository.getPayments()
                        .collect{ payments ->
                            _state.update { it.copy(isLoading = false, payments = payments.sortedByDescending { paymentItem -> paymentItem.timestamp }) }
                        }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }
}