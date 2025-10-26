package com.cashi.technical.viewmodel.payment.state

/**
Created By: Pratham
 */
data class PaymentUiState(
    val email : String = "",
    val amount : String = "",
    val currency : String = "USD",
    val isLoading : Boolean = false,
    val message : String? = null
)
