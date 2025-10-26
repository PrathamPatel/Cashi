package com.cashi.technical.model

import kotlinx.serialization.Serializable

/**
Created By: Pratham
 */
@Serializable
data class Payment(
    val recipientEmail : String = "",
    val amount : Double = 0.0,
    val currency : String = "USD",
    val timestamp : Long = System.currentTimeMillis()
)
