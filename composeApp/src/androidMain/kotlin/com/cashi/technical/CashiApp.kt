package com.cashi.technical

import android.app.Application
import com.cashi.technical.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
Created By: Pratham
 */
class CashiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CashiApp)
            modules(appModule)
        }
    }
}