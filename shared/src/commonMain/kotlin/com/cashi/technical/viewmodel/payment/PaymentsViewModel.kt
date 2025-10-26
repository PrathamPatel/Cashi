package com.cashi.technical.viewmodel.payment

import com.cashi.technical.di.provider.DispatcherProvider
import com.cashi.technical.model.Payment
import com.cashi.technical.repository.PaymentsRepository
import com.cashi.technical.util.NetworkMonitor
import com.cashi.technical.validation.PaymentValidator
import com.cashi.technical.viewmodel.payment.intents.PaymentIntent
import com.cashi.technical.viewmodel.payment.state.PaymentUiState
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
class PaymentsViewModel(
    private val paymentsRepository: PaymentsRepository,
    private val dispatcher : DispatcherProvider,
    private val networkMonitor: NetworkMonitor
) {
    private val scope = CoroutineScope(dispatcher.io)

    private val _state = MutableStateFlow(PaymentUiState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: PaymentIntent){
        when(intent){
            is PaymentIntent.ChangeEmail -> _state.update { it.copy(email = intent.email) }
            is PaymentIntent.ChangeAmount -> _state.update { it.copy(amount = intent.amount.replace(",", ".")) }
            is PaymentIntent.ChangeCurrency -> _state.update { it.copy(currency = intent.currency) }
            PaymentIntent.SendPayment -> sendPayment()
        }
    }

    fun sendPayment(){
        val currentState = _state.value
        val error = PaymentValidator.validateInput(email = currentState.email, amount = currentState.amount)
        if(error.isNotBlank()){
            _state.update { it.copy(message = error) }
            return
        }
        if(!networkMonitor.isInternetAvailable()){
            _state.update { it.copy(message = "Internet connection required to send payment.") }
        }
        else{
            scope.launch {
                _state.update { it.copy(isLoading = true, message = null) }
                try {
                    val payment = Payment(
                        recipientEmail = currentState.email,
                        amount = currentState.amount.toDouble(),
                        currency = currentState.currency
                    )

                    val success = paymentsRepository.processPayment(payment)
                    _state.update { it.copy(
                        isLoading = false,
                        message = if(success){
                            "Payment sent!"
                        }
                        else{
                            "Failed to send payment"
                        }
                    )
                    }
                }catch (e : Exception){
                    _state.update { it.copy(isLoading = false, message = e.message) }
                }
            }
        }
    }
}