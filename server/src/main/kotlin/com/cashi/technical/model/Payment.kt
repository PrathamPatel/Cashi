package com.cashi.technical.model

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val id: String? = null,
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val timestamp: Long
)