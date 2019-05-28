package com.umutbey.userlist.repository


import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.umutbey.userlist.helpers.enqueue
import com.umutbey.userlist.model.User
import com.umutbey.userlist.network.ApiInterface


class Repository(private val apiInterfaceInterface: ApiInterface) {
    fun getUsers(): MutableLiveData<ArrayList<User>>? {
        val userListForReturn = MutableLiveData<ArrayList<User>>()
        val call = apiInterfaceInterface.getUsers()
        call.enqueue { call, throwable, response ->
            if (response?.body() != null) {
                userListForReturn.value = response.body() as ArrayList<User>?
            } else {
                Log.e("error", throwable?.message ?: "response null")
            }
        }
        return userListForReturn
    }

    fun getLogin(email: String, password: String): MutableLiveData<Boolean> {
        val userListForReturn = MutableLiveData<Boolean>()
        val call = apiInterfaceInterface.getLogin()
        call.enqueue { _, throwable, response ->
            if (response?.body() != null) {
                val result = ((response.body() as ArrayList<User>?)?.none {
                    it.email == email && it.password == password
                })
                userListForReturn.value = !(result ?: true)
            } else {
                Log.e("error", throwable?.message ?: "response null")
                userListForReturn.value = false
            }
        }
        return userListForReturn
    }
}
