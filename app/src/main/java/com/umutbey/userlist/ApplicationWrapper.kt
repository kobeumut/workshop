package com.umutbey.userlist

import android.app.Application
import com.umutbey.userlist.di.NetworkModule
import com.umutbey.userlist.viewmodel.LoginViewModel
import com.umutbey.userlist.viewmodel.MainViewModel
import com.umutbey.userlist.viewmodel.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module


class ApplicationWrapper : Application() {
    private val appModule = module {
        single { ImageModule(get()) }
    }
    private val viewModule = module {
        viewModel { LoginViewModel() }
        viewModel { MainViewModel(get()) }
        viewModel { ProfileViewModel() }
    }
    private val networkModule = NetworkModule().getModule()
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        startKoin {
            androidContext(this@ApplicationWrapper)
            modules(appModule, viewModule, networkModule)
        }
    }

    companion object {
        lateinit var INSTANCE: ApplicationWrapper
    }

}