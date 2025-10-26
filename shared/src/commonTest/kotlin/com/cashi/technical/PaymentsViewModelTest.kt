package com.cashi.technical

import com.cashi.technical.di.provider.DispatcherProvider
import com.cashi.technical.model.Payment
import com.cashi.technical.repository.PaymentsRepository
import com.cashi.technical.viewmodel.payment.PaymentsViewModel
import com.cashi.technical.viewmodel.payment.intents.PaymentIntent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
Created By: Pratham
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PaymentsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatcherProvider = object : DispatcherProvider {
        override val io = testDispatcher
        override val main = testDispatcher
        override val default = testDispatcher
    }

    private lateinit var repository: PaymentsRepository
    private lateinit var viewModel: PaymentsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = PaymentsViewModel(repository, dispatcherProvider)
    }

    @AfterTest
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `handleIntent updates email, amount, and currency`() = runTest {
        viewModel.handleIntent(PaymentIntent.ChangeEmail("test@cashi.com"))
        viewModel.handleIntent(PaymentIntent.ChangeAmount("10"))
        viewModel.handleIntent(PaymentIntent.ChangeCurrency("USD"))

        val state = viewModel.state.value
        assertEquals("test@cashi.com", state.email)
        assertEquals("10", state.amount)
        assertEquals("USD", state.currency)
    }


    @Test
    fun `sendPayment shows error for invalid email`() = runTest {
        viewModel.handleIntent(PaymentIntent.ChangeEmail("invalid_email"))
        viewModel.handleIntent(PaymentIntent.ChangeAmount("10"))

        viewModel.handleIntent(PaymentIntent.SendPayment)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.message!!.contains("Invalid recipient email"))
        coVerify(exactly = 0) { repository.processPayment(any()) }
    }


    @Test
    fun `sendPayment updates message when successful`() = runTest {
        val payment = Payment("user@cashi.com", 10.0, "USD")

        coEvery { repository.processPayment(any()) } returns true

        viewModel.handleIntent(PaymentIntent.ChangeEmail(payment.recipientEmail))
        viewModel.handleIntent(PaymentIntent.ChangeAmount(payment.amount.toString()))
        viewModel.handleIntent(PaymentIntent.ChangeCurrency(payment.currency))
        viewModel.handleIntent(PaymentIntent.SendPayment)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Payment sent!", state.message)
        coVerify(exactly = 1) { repository.processPayment(match {
            it.recipientEmail == payment.recipientEmail &&
                    it.amount == payment.amount &&
                    it.currency == payment.currency
        }) }
    }

    @Test
    fun `sendPayment updates message when repository fails`() = runTest {
        coEvery { repository.processPayment(any()) } returns false

        viewModel.handleIntent(PaymentIntent.ChangeEmail("test@cashi.com"))
        viewModel.handleIntent(PaymentIntent.ChangeAmount("20"))
        viewModel.handleIntent(PaymentIntent.ChangeCurrency("USD"))
        viewModel.handleIntent(PaymentIntent.SendPayment)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Failed to send payment", state.message)
        coVerify(exactly = 1) { repository.processPayment(any()) }
    }

    @Test
    fun `sendPayment updates message when exception occurs`() = runTest {
        coEvery { repository.processPayment(any()) } throws Exception("Network error")

        viewModel.handleIntent(PaymentIntent.ChangeEmail("test@cashi.com"))
        viewModel.handleIntent(PaymentIntent.ChangeAmount("50"))
        viewModel.handleIntent(PaymentIntent.SendPayment)

        delay(1000)

        val state = viewModel.state.value
        assertEquals("Network error", state.message)
    }
}