package com.cashi.technical

import com.cashi.technical.validation.PaymentValidator
import kotlin.test.Test
import kotlin.test.assertEquals

/**
Created By: Pratham
 */
class PaymentValidationTest {

    @Test
    fun `returns error for invalid email`() {
        val result = PaymentValidator.validateInput("invalidEmail", "100")
        assertEquals("Invalid recipient email", result)
    }

    @Test
    fun `returns error when amount is not a number`() {
        val result = PaymentValidator.validateInput("user@example.com", "abc")
        assertEquals("Invalid amount. Amount must be greater than 0", result)
    }

    @Test
    fun `returns error when amount is zero`() {
        val result = PaymentValidator.validateInput("user@example.com", "0")
        assertEquals("Invalid amount. Amount must be greater than 0", result)
    }

    @Test
    fun `returns error when amount is negative`() {
        val result = PaymentValidator.validateInput("user@example.com", "-5")
        assertEquals("Invalid amount. Amount must be greater than 0", result)
    }

    @Test
    fun `returns error when amount has more than two decimal places`() {
        val result = PaymentValidator.validateInput("user@example.com", "12.345")
        assertEquals("Only 2 values after the decimal is allowed for the amount", result)
    }

    @Test
    fun `returns error when amount is too large`() {
        val result = PaymentValidator.validateInput("user@example.com", "1234567890123")
        assertEquals("Amount is too large to process", result)
    }

    @Test
    fun `returns empty string when input is valid`() {
        val result = PaymentValidator.validateInput("user@example.com", "100.50")
        assertEquals("", result)
    }

    @Test
    fun `allows valid email with special characters`() {
        val result = PaymentValidator.validateInput("test.user+1@example.co.uk", "50")
        assertEquals("", result)
    }
}