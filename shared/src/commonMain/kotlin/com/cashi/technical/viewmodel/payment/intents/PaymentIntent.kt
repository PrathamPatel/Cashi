package com.cashi.technical.viewmodel.payment.intents

/**
Created By: Pratham
 */
sealed class PaymentIntent {
    data class ChangeEmail(val email: String) : PaymentIntent()
    data class ChangeAmount(val amount: String) : PaymentIntent()
    data class ChangeCurrency(val currency: String) : PaymentIntent()
    data object SendPayment : PaymentIntent()
}