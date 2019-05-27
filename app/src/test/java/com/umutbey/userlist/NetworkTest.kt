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


class NetworkTest:KoinTest {
    private val retrofit = NetworkModule().initRetrofit()

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var userService: ApiInterface

    @Test
    fun networkBehaviorNullThrows() {
        val builder = MockRetrofit.Builder(NetworkModule().initRetrofit())
        try {
            builder.networkBehavior(null)
            fail()
        } catch (e: NullPointerException) {
            e.message?.contains("behavior == null")?.let { assertTrue(it) }
        }

    }

    @Test
    fun retrofitPropagated() {
        val mockRetrofit = MockRetrofit.Builder(retrofit).build()
        assertEquals(mockRetrofit.retrofit(), retrofit)
    }

    @Test
    @Throws(Exception::class)
    fun checkRestClient() {
        val service = NetworkModule().initRetrofit().create(ApiInterface::class.java)
        val response = service.getLogin().execute()
        val authResponse = response.body()
        assertTrue(response.isSuccessful && authResponse!!.isNotEmpty())
    }
    @Test
    @Throws(IOException::class)
    fun bodyExecute() {
        val call = Calls.response("BodyString")
        assertEquals("BodyString", call.execute().body())
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUp() {
            val viewModule = module {
                viewModel { MainViewModel(get()) }
            }
            val networkModule = NetworkModule().getModule()
            startKoin {
                modules(viewModule, networkModule)
            }

        }
    }

}