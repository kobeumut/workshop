package com.umutbey.userlist.repository


import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.umutbey.userlist.model.User
import com.umutbey.userlist.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Repository(private val apiInterfaceInterface: ApiInterface) {
    fun getUsers(): MutableLiveData<ArrayList<User>>? {
        val userListForReturn = MutableLiveData<ArrayList<User>>()
        val call = apiInterfaceInterface.getUsers()
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.body() != null) {
                    userListForReturn.value = response.body() as ArrayList<User>?
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("error", t.message)
            }
        })

        return userListForReturn
    }

    fun getLogin(email: String, password: String): MutableLiveData<Boolean> {
        val userListForReturn = MutableLiveData<Boolean>()
        val call = apiInterfaceInterface.getLogin()
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.body() != null) {
                    val result = ((response.body() as ArrayList<User>?)?.none {
                        it.email == email && it.password == password
                    })
                    userListForReturn.value = !(result ?: true)
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("error", t.message)
                userListForReturn.value = false
            }
        })

        return userListForReturn
    }
}
