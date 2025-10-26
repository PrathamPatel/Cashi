package com.cashi.technical

import com.cashi.technical.routes.paymentRoutes
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.junit.Test
import kotlin.test.assertEquals

/**
Created By: Pratham
 */
class PaymentRouteTest {

    @Test
    fun testPostPaymentRoute() = testApplication{
        application{
            module()
            paymentRoutes()
        }

        val response = client.post("/payments") {
            contentType(ContentType.Application.Json)
            setBody("""{"recipientEmail": "test@cashii.com", "amount": 20.5, "currency": "USD", "timestamp": ${System.currentTimeMillis()} }""")

        }

        assertEquals(HttpStatusCode.OK, response.status)
        println(response.bodyAsText())
    }

}