package com.umutbey.userlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.umutbey.userlist.di.NetworkModule
import com.umutbey.userlist.network.ApiInterface
import com.umutbey.userlist.viewmodel.MainViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.mock.Calls
import retrofit2.mock.MockRetrofit
import java.io.IOException


class LoginTest:KoinTest {
    private val retrofit = NetworkModule().initRetrofit()

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Test
    fun succeedLoginTest() {
        val service = NetworkModule().initRetrofit().create(ApiInterface::class.java)
        val response = service.getLogin().execute()
        val authResponse = response.body()
        authResponse?.get(0)?.email?.let {
            assertEquals("mona@guy.biz",it)
        }
        authResponse?.get(0)?.password?.let {
            assertEquals("841727396",it)
        }
    }
    @Test
    fun failedLoginTest() {
        val service = NetworkModule().initRetrofit().create(ApiInterface::class.java)
        val response = service.getLogin().execute()
        val authResponse = response.body()
        authResponse?.get(0)?.email?.let {
            assertNotEquals("mona@guy.biz1",it)
        }
        authResponse?.get(0)?.password?.let {
            assertNotEquals("1234321421",it)
        }
    }

}