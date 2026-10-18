package org.darkness.jsonuiview

import android.app.Application
import org.darkness.jsonuiview.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class JsonUiViewApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@JsonUiViewApplication)
            modules(AppModule.module)
        }
    }
}