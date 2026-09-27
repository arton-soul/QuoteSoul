package com.soulquote.app

import android.app.Application

import com.soulquote.app.core.di.AppContainer
import com.soulquote.app.core.di.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SoulQuoteApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)

        applicationScope.launch {
            appContainer.databaseSeeder.seedIfNecessary()
        }
    }
}
