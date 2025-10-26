package com.cashi.technical.firebase

import com.cashi.technical.model.Payment
import kotlinx.coroutines.flow.Flow

/**
Created By: Pratham
 */
interface FirebaseDataSource {

    suspend fun addPayment(payment: Payment)
    fun getPayments() : Flow<List<Payment>>
}