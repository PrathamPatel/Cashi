package com.cashi.technical.repository

import com.cashi.technical.api.PaymentApi
import com.cashi.technical.firebase.provider.FirebaseProvider
import com.cashi.technical.interfaces.IPaymentsRepository
import com.cashi.technical.model.Payment

/**
Created By: Pratham
 */
class PaymentsRepository(private val api: PaymentApi) : IPaymentsRepository {

    private val firebase = FirebaseProvider.getDataSource()

    override suspend fun processPayment(payment: Payment) : Boolean{
        val result = api.sendPayment(payment)
        if(result){
            firebase.addPayment(payment)
        }

        return result
    }

    override fun getPayments() = firebase.getPayments()
}