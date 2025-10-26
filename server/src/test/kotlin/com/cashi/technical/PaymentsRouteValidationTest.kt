package com.cashi.technical

import com.cashi.technical.routes.paymentRoutes
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import junit.framework.TestCase.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

/**
Created By: Pratham
 */
class PaymentsRouteValidationTest {

    @Test
    fun testInvalidEmail_Returns400() = testApplication {
        application {
            module()
            paymentRoutes()
        }

        val response = client.post("/payments") {
            contentType(ContentType.Application.Json)
            setBody("""{"recipientEmail":"invalid","amount":10.0,"currency":"USD", "timestamp": ${System.currentTimeMillis()}}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid recipient email"))
    }

    @Test
    fun testInvalidAmount_Returns400() = testApplication {
        application {
            module()
            paymentRoutes()
        }

        val response = client.post("/payments") {
            contentType(ContentType.Application.Json)
            setBody("""{"recipientEmail":"valid@example.com","amount":0.0,"currency":"USD", "timestamp": ${System.currentTimeMillis()}}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid amount"))
    }

    @Test
    fun testValidPayment_Returns200() = testApplication {
        application {
            module()
            paymentRoutes()
        }

        val response = client.post("/payments") {
            contentType(ContentType.Application.Json)
            setBody("""{"recipientEmail":"valid@example.com","amount":25.5,"currency":"USD", "timestamp": ${System.currentTimeMillis()}}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("valid@example.com"))
    }
}