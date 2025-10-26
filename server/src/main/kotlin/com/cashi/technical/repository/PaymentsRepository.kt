package com.cashi.technical.repository

import com.cashi.technical.model.Payment
import java.util.concurrent.ConcurrentHashMap

/**
Created By: Pratham
 */
class PaymentsRepository {

    private val payments = ConcurrentHashMap<String?, Payment>()

    fun add(payment: Payment){
        payments[payment.id] = payment
    }
}