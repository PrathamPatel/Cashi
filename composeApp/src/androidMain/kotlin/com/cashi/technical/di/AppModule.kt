package com.cashi.technical.di

import com.cashi.technical.api.PaymentApi
import com.cashi.technical.di.provider.DispatcherProvider
import com.cashi.technical.di.provider.BaseDispatchersProvider
import com.cashi.technical.repository.PaymentsRepository
import com.cashi.technical.util.NetworkMonitor
import com.cashi.technical.viewmodel.payment.PaymentsViewModel
import com.cashi.technical.viewmodel.transactionhistory.TransactionHistoryViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
Created By: Pratham
 */

val appModule = module {
    single {
        HttpClient(OkHttp){
            install(ContentNegotiation) { json() }
        }
    }

    single {
        PaymentApi(client = get(), baseUrl = "http://10.0.2.2:8080")
    }

    single<DispatcherProvider> { BaseDispatchersProvider() }

    single { NetworkMonitor().apply {
        initNetworkMonitor(androidContext())
    } }

    single { PaymentsRepository(api = get()) }

    single { PaymentsViewModel(paymentsRepository = get(), dispatcher = get(), networkMonitor = get()) }
    single { TransactionHistoryViewModel(paymentsRepository = get(), dispatcher = get(), networkMonitor = get()) }
}