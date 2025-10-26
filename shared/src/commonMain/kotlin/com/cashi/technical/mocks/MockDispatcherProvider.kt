package com.cashi.technical.mocks

import com.cashi.technical.di.provider.DispatcherProvider
import kotlinx.coroutines.Dispatchers

/**
Created By: Pratham
 */
class MockDispatcherProvider: DispatcherProvider {
    override val io = Dispatchers.Main
    override val main = Dispatchers.Main
    override val default = Dispatchers.Main
}