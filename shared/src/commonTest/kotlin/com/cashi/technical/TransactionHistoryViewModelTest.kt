package com.cashi.technical

import com.cashi.technical.di.provider.DispatcherProvider
import com.cashi.technical.model.Payment
import com.cashi.technical.repository.PaymentsRepository
import com.cashi.technical.viewmodel.transactionhistory.TransactionHistoryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
Created By: Pratham
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TransactionHistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatcherProvider = object : DispatcherProvider {
        override val io = testDispatcher
        override val main = testDispatcher
        override val default = testDispatcher
    }

    private lateinit var repository: PaymentsRepository
    private lateinit var viewModel: TransactionHistoryViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = TransactionHistoryViewModel(repository, dispatcherProvider)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPayments updates state with loaded payments`() = runTest {
        val mockPayments = listOf(
            Payment("user1@cashi.com", 10.0, "USD"),
            Payment("user2@cashi.com", 20.5, "EUR")
        )

        every { repository.getPayments() } returns flowOf(mockPayments)

        viewModel = TransactionHistoryViewModel(repository, dispatcherProvider)

        advanceUntilIdle()

        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals(mockPayments.size, state.payments.size)
        assertEquals(mockPayments.first().recipientEmail, state.payments.first().recipientEmail)
        assertNull(state.error)
    }

    @Test
    fun `getPayments updates state with error when repository throws`() = runTest {
        every { repository.getPayments() } returns flow {
            throw Exception("Firestore failure")
        }

        viewModel = TransactionHistoryViewModel(repository, dispatcherProvider)

        advanceUntilIdle()

        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertTrue(state.error!!.contains("Firestore failure"))
        assertTrue(state.payments.isEmpty())
    }

    @Test
    fun `initial state should start loading`() = runTest {
        val paymentsFlow = MutableSharedFlow<List<Payment>>()
        every { repository.getPayments() } returns paymentsFlow

        viewModel = TransactionHistoryViewModel(repository, dispatcherProvider)
        advanceUntilIdle()
        val stateBefore = viewModel.state.value
        assertTrue(stateBefore.isLoading)

        // Emit after slight delay
        paymentsFlow.emit(emptyList())


        val stateAfter = viewModel.state.value
        assertFalse(stateAfter.isLoading)
    }
}