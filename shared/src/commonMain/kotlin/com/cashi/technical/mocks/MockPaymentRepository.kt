package com.cashi.technical.mocks

import com.cashi.technical.interfaces.IPaymentsRepository
import com.cashi.technical.model.Payment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
Created By: Pratham
 */
class MockPaymentRepository : IPaymentsRepository {
    override suspend fun processPayment(payment: Payment): Boolean {
        return true
    }

    override fun getPayments(): Flow<List<Payment>> = flowOf(emptyList())
}