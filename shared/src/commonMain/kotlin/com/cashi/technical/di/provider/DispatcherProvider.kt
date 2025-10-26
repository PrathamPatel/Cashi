package com.cashi.technical.di.provider

import kotlinx.coroutines.CoroutineDispatcher

/**
Created By: Pratham
 */
interface DispatcherProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}