package com.cashi.technical.api

import com.cashi.technical.model.Payment
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

/**
Created By: Pratham
 */
class PaymentApi(private val client : HttpClient, private val baseUrl : String) {

    suspend fun sendPayment(payment: Payment) : Boolean{
        val response = client.post("$baseUrl/payments"){
            contentType(ContentType.Application.Json)
            setBody(payment)
        }

        return response.status == HttpStatusCode.OK
    }
}