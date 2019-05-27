package com.umutbey.userlist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.umutbey.userlist.model.User
import com.umutbey.userlist.network.ApiInterface
import com.umutbey.userlist.repository.Repository
import org.koin.core.KoinComponent
import org.koin.core.inject


class MainViewModel(apiInterface: ApiInterface): ViewModel(),KoinComponent {

    var user: MutableLiveData<ArrayList<User>>? = null
    var repo: Repository = Repository(apiInterface)

    init {
        user = repo.getUsers()

    }

    fun loadMore(page: Int) {
        //Servis yapisi uygun olmadigi icin ayni veriler tekrar cagiriliyor
        repo.getUsers()!!.observeForever {
            if (it != null)
                user!!.value = it
        }
    }

}
