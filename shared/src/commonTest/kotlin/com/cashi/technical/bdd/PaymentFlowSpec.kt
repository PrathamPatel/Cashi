package com.cashi.technical.bdd

import com.cashi.technical.di.provider.DispatcherProvider
import com.cashi.technical.model.Payment
import com.cashi.technical.repository.PaymentsRepository
import com.cashi.technical.viewmodel.payment.PaymentsViewModel
import com.cashi.technical.viewmodel.payment.intents.PaymentIntent
import com.cashi.technical.viewmodel.transactionhistory.TransactionHistoryViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

object PaymentFlowSpec : Spek({

    describe("Payment creation and transaction history") {

        val dispatcher = StandardTestDispatcher()
        val dispatcherProvider = object : DispatcherProvider {
            override val io = dispatcher
            override val main = dispatcher
            override val default = dispatcher
        }

        lateinit var paymentsViewModel: PaymentsViewModel
        lateinit var historyViewModel: TransactionHistoryViewModel
        lateinit var repository: PaymentsRepository

        beforeEachTest {
            Dispatchers.setMain(dispatcher)
            repository = mockk(relaxed = true)
            paymentsViewModel = PaymentsViewModel(repository, dispatcherProvider)
            historyViewModel = TransactionHistoryViewModel(repository, dispatcherProvider)
        }

        context("when a valid payment is made") {
            beforeEachTest {
                coEvery { repository.processPayment(any()) } returns true
                coEvery { repository.getPayments() } returns flowOf(
                    listOf(
                        Payment("user@cashi.com", 50.0, "USD"),
                        Payment("test@cashi.com", 25.0, "USD")
                    )
                )
            }

            it("should process successfully and show success message"){
                runTest {
                    paymentsViewModel.handleIntent(
                        PaymentIntent.ChangeEmail("user@cashi.com")
                    )
                    paymentsViewModel.handleIntent(
                        PaymentIntent.ChangeAmount("50")
                    )
                    paymentsViewModel.handleIntent(
                        PaymentIntent.SendPayment
                    )

                    advanceUntilIdle()

                    val state = paymentsViewModel.state.value
                    assertFalse(state.isLoading)
                    assertEquals("Payment sent!", state.message)
                }
            }

            it("should show the updated transaction history"){
                runTest {
                    advanceUntilIdle()
                    val state = historyViewModel.state.value
                    assertFalse(state.isLoading)
                    assertTrue(state.payments.isNotEmpty())
                    assertEquals(2, state.payments.size)
                }
            }
        }

        context("when the payment fails due to invalid email") {
            beforeEachTest {
                coEvery { repository.processPayment(any()) } returns false
            }

            it("should not process and return validation error"){
                runTest {
                    paymentsViewModel.handleIntent(
                        PaymentIntent.ChangeEmail("invalid-email")
                    )
                    paymentsViewModel.handleIntent(
                        PaymentIntent.ChangeAmount("100")
                    )
                    paymentsViewModel.handleIntent(
                        PaymentIntent.SendPayment
                    )

                    advanceUntilIdle()

                    val state = paymentsViewModel.state.value
                    assertFalse(state.isLoading)
                    assertEquals("Invalid recipient email", state.message)
                }
            }
        }

        context("when there is a network failure") {
            beforeEachTest {
                coEvery { repository.processPayment(any()) } throws Exception("Network error")
            }
            it("should show an error message in the state"){
                runTest {
                    paymentsViewModel.handleIntent(
                        PaymentIntent.ChangeEmail("test@cashi.com")
                    )
                    paymentsViewModel.handleIntent(
                        PaymentIntent.ChangeAmount("25")
                    )
                    paymentsViewModel.handleIntent(
                        PaymentIntent.SendPayment
                    )

                    advanceUntilIdle()

                    val state = paymentsViewModel.state.value
                    assertFalse(state.isLoading)
                    assertEquals("Network error", state.message)
                }
            }
        }
    }
})
