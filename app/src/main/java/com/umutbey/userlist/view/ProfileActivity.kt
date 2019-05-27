package com.umutbey.userlist.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.umutbey.userlist.ApplicationWrapper
import com.umutbey.userlist.ImageModule
import com.umutbey.userlist.R
import com.umutbey.userlist.databinding.ActivityProfileBinding
import com.umutbey.userlist.helpers.cleanup
import com.umutbey.userlist.helpers.put
import com.umutbey.userlist.model.User
import com.umutbey.userlist.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.address
import kotlinx.android.synthetic.main.activity_profile.age
import kotlinx.android.synthetic.main.activity_profile.phone
import kotlinx.android.synthetic.main.activity_profile.view.*
import org.jetbrains.anko.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ProfileActivity : AppCompatActivity() {
    private val viewModel: ProfileViewModel by viewModel()
    private lateinit var binding: ActivityProfileBinding

    private val pref: SharedPreferences by lazy {
        ApplicationWrapper.INSTANCE.getSharedPreferences(
            MainActivity.PREFS_FILENAME,
            0
        )
    }
    private val imageModule: ImageModule by inject { parametersOf(this@ProfileActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        val user = intent.getParcelableExtra<User>("DATA")
        viewModel.setBindingUser(user)
        binding.viewModel = viewModel
        imageModule.get(user.avatar, profile_img, isCircle = true)

        binding.edit.setOnClickListener {
            alert {
                customView {
                    verticalLayout {
                        padding = dip(16)
                        val ageText = editText(binding.age.text) {
                            hint = "Age"
                        }
                        val phoneText = editText(binding.phone.text) {
                            hint = "Phone"
                        }
                        val addressText = editText(binding.address.text) {
                            lines = 3
                            hint = "Address"
                        }
                        okButton {
                            var text: String = ageText.text.toString().replace("[^-?0-9]+".toRegex(), "")

                            user.age = Integer.parseInt(if (text.isNullOrEmpty()) "0" else text)

                            text = phoneText.text.toString()
                            user.phone = text.cleanup()

                            text = addressText.text.toString()
                            user.address = text.cleanup()

                            viewModel.setBindingUser(user)
                            binding.viewModel = viewModel
                            pref.put(user.id.toString(), user)
                        }

                    }
                }
            }.show()
        }

    }
}



