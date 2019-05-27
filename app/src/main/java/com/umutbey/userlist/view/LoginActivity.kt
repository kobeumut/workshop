package com.umutbey.userlist.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import com.umutbey.userlist.R
import com.umutbey.userlist.helpers.safelyDispose
import com.umutbey.userlist.viewmodel.LoginViewModel
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModel()
    private var checkLogin: Boolean = false
    private lateinit var mailDisposable: Disposable
    private lateinit var passDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button.setOnClickListener {
            if (mail.text?.length ?: 0 < 1) {
                mail_error.error = getString(R.string.error_mail)
            }
            if (password.text?.length ?: 0 < 1) {
                password_error.error = getString(R.string.error_password)
            }
            if (checkLogin) {
                viewModel.checkLogin(mail.text.toString(), password.text.toString()).observeForever {
                    if (it) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Mail or Password is wrong, please try again",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        mailDisposable = RxTextView.afterTextChangeEvents(mail)
            .skipInitialValue()
            .map {
                mail_error.error = null
                it.view().text.toString()
            }
            .debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(validateEmailAddress)
            .compose(retryWhenError {
                mail_error.error = it.message
            })
            .subscribe { checkLogin = true }
        passDisposable = RxTextView.afterTextChangeEvents(password)
            .skipInitialValue()
            .map {
                password_error.error = null
                it.view().text.toString()
            }
            .debounce(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
            .compose(validatePassword)
            .compose(retryWhenError {
                password_error.error = it.message
            })
            .subscribe { checkLogin = true }
    }

    private val validateEmailAddress = ObservableTransformer<String, String> { observable ->
        observable.flatMap {
            Observable.just(it).map { it.trim() }
                .filter {
                    Patterns.EMAIL_ADDRESS.matcher(it).matches()
                }
                .singleOrError()
                .onErrorResumeNext {
                    if (it is NoSuchElementException) {
                        Single.error(Exception(getString(R.string.error_mail)))
                    } else {
                        Single.error(it)

                    }
                }
                .toObservable()
        }
    }
    private val validatePassword = ObservableTransformer<String, String> { observable ->
        observable.flatMap {
            Observable.just(it).map { it.trim() }
                .filter { it.length > 7 }
                .singleOrError()
                .onErrorResumeNext {
                    if (it is NoSuchElementException) {
                        Single.error(Exception(getString(R.string.error_password)))

                    } else {
                        Single.error(it)
                    }
                }
                .toObservable()


        }
    }

    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> =
        ObservableTransformer { observable ->
            observable.retryWhen { errors ->
                checkLogin = false
                errors.flatMap {
                    onError(it)
                    Observable.just("")
                }
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        mailDisposable.safelyDispose()
        passDisposable.safelyDispose()
    }
}



