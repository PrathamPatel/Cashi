package com.cashi.technical.interfaces

import com.cashi.technical.model.Payment
import kotlinx.coroutines.flow.Flow

/**
Created By: Pratham
 */
interface IPaymentsRepository {
    suspend fun processPayment(payment: Payment): Boolean
    fun getPayments(): Flow<List<Payment>>
}