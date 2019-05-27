package com.umutbey.userlist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.umutbey.userlist.model.User
import com.umutbey.userlist.network.ApiInterface
import com.umutbey.userlist.repository.Repository
import org.koin.core.KoinComponent
import org.koin.core.inject


class LoginViewModel: ViewModel(),KoinComponent {
    private val apiInterface:ApiInterface by inject<ApiInterface>()

    var repo: Repository = Repository(apiInterface)

    fun checkLogin(email: String, password: String): MutableLiveData<Boolean> {
        return repo.getLogin(email,password)
    }

}
