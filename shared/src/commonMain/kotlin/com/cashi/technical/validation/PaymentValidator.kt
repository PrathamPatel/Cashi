package com.cashi.technical.validation

/**
Created By: Pratham
 */
object PaymentValidator{

    fun validateInput(email : String, amount : String) : String{
        return when{
            !email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))  -> "Invalid recipient email"
            (amount.toDoubleOrNull() ?: 0.0) <= 0 -> "Invalid amount. Amount must be greater than 0"
            amount.length > 12 -> "Amount is too large to process"
            amount.contains(".") && amount.substringAfter(".").length > 2 -> "Only 2 values after the decimal is allowed for the amount"
            else -> ""
        }
    }
}