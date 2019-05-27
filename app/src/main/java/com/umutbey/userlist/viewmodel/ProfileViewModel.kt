package com.umutbey.userlist.viewmodel

import android.graphics.Color
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModel
import com.umutbey.userlist.model.User
import org.koin.core.KoinComponent


class ProfileViewModel : ViewModel(), KoinComponent {
    lateinit var viewObservable:ViewObservable
    fun setBindingUser(user:User){
        viewObservable = ViewObservable(user)
        viewObservable.notifyFields()
    }

    inner class ViewObservable(val user: User) : BaseObservable() {
        val name: String
            @Bindable
            get() = "${user.name} ${user.surname}"

        val age: String
            @Bindable
            get() = "Age: ${user.age}"

        val phone: String
            @Bindable
            get() = "Phone: ${user.phone}"

        val address: String
            @Bindable
            get() = "Address: ${user.address}"

        val mail: String
            @Bindable
            get() = "Mail: ${user.email}"


        val about: String
            @Bindable
            get() = "About: ${user.about}"


        val favoriteColor: String
            @Bindable
            get() = "Fav Color: ${user.favoriteColor}"

        val backgroundColor: Int
            @Bindable
            get() = Color.parseColor(user.favoriteColor)

        internal fun notifyFields() {
            notifyPropertyChanged(BR._all)
        }
    }

}
