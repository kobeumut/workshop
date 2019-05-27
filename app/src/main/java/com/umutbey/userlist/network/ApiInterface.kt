package com.umutbey.userlist.network

import com.umutbey.userlist.model.User
import io.reactivex.Flowable
import io.reactivex.Maybe
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("userList.json")
    fun getLogin(): Call<List<User>>

    @GET("userList.json")
    fun getUsers(): Call<List<User>>

}
