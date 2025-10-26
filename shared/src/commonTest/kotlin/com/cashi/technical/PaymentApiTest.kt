package com.cashi.technical

import com.cashi.technical.api.PaymentApi
import com.cashi.technical.model.Payment
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
Created By: Pratham
 */
class PaymentApiTest {

    @Test
    fun testSendPayment_Success() = runTest {
        val mockEngine = MockEngine { request ->
            assertEquals("/payments", request.url.encodedPath)
            respond("OK", HttpStatusCode.OK,headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }

        val client = io.ktor.client.HttpClient(mockEngine){
            install(ContentNegotiation){
                json()
            }
        }
        val api = PaymentApi(client, "http://test")

        val payment = Payment("test@cashi.com", 10.0, "USD")
        val result = api.sendPayment(payment)

        assertTrue(result)
    }

    @Test
    fun testSendPayment_Failure() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = "Server error",
                status = HttpStatusCode.InternalServerError
            )
        }

        val client = io.ktor.client.HttpClient(mockEngine) {
            install(ContentNegotiation) { json() }
        }
        val api = PaymentApi(client, "http://test")

        val payment = Payment("test@cashi.com", 10.0, "USD")
        val result = api.sendPayment(payment)

        assertFalse(result, "Expected result to be false for failed request")
    }
}