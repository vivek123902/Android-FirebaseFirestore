package com.dwarkadhish.tea

import android.app.Application
import com.dwarkadhish.tea.koin.appModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application(){
    override fun onCreate() {
    super.onCreate()
        FirebaseApp.initializeApp(this)
    startKoin {
        androidContext(this@MyApplication)
        modules(appModule)
    }
}

}