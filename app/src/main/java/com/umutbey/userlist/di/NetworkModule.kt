package com.umutbey.userlist.di

import com.google.gson.GsonBuilder
import com.umutbey.userlist.network.ApiInterface
import org.koin.core.module.Module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkModule {
    fun getModule(): Module {
        return org.koin.dsl.module {

            single {
                NetworkModule().initRetrofit()
            }

            factory { get<Retrofit>().create(ApiInterface::class.java) }
        }
    }

    fun initRetrofit(): Retrofit {
        val gson = GsonBuilder().apply {
            setLenient()
        }.create()
        val baseUrl = "https://raw.githubusercontent.com/kobeumut/workshop/master/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}