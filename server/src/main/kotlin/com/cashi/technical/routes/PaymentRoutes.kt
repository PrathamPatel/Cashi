package com.cashi.technical.routes

import com.cashi.technical.model.Payment
import com.cashi.technical.repository.PaymentsRepository
import com.cashi.technical.routes.model.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.util.UUID

/**
Created By: Pratham
 */

fun Application.paymentRoutes(){
    val repository = PaymentsRepository()

    routing {
        route("/payments"){
            post {
                val payment = call.receive<Payment>()
                val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

                when {
                    !emailRegex.matches(payment.recipientEmail) -> {
                        call.respond(HttpStatusCode.BadRequest,
                            ErrorResponse("Invalid email format")
                        )
                        return@post
                    }
                    payment.amount <= 0 -> {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid amount. Must be greater than 0"))
                        return@post
                    }
                    payment.currency.isBlank() -> {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Currency cannot be empty"))
                        return@post
                    }
                }
                val newPayment = payment.copy(id = UUID.randomUUID().toString(), timestamp = System.currentTimeMillis())
                repository.add(newPayment)
                call.respond(HttpStatusCode.OK, newPayment)
            }
        }
    }
}