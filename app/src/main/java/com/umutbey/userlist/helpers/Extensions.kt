package com.umutbey.userlist.helpers

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.disposables.Disposable


fun AppCompatActivity.isNetworkConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return (activeNetwork != null && activeNetwork.isConnected)
}

fun Disposable.safelyDispose() {
    if (!this.isDisposed) {
        this.dispose()
    }
}

val gson: Gson = Gson()

inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: Any? = null): T {
    return when (T::class) {
        Boolean::class -> this.getBoolean(key, defaultValue as Boolean) as T
        Float::class -> this.getFloat(key, defaultValue as Float) as T
        Int::class -> this.getInt(key, defaultValue as Int) as T
        Long::class -> this.getLong(key, defaultValue as Long) as T
        String::class -> this.getString(key, defaultValue as String) as T
        else -> defaultValue as T
    }
}

inline fun <reified T : Any> SharedPreferences.getObject(key: String): T? {
    return gson.fromJson<T>(this.getString(key, ""), object : TypeToken<T>() {}.type)
}

inline fun <reified T> SharedPreferences.put(key: String, value: T) {
    val editor = this.edit()

    when (T::class) {
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Float::class -> editor.putFloat(key, value as Float)
        Int::class -> editor.putInt(key, value as Int)
        Long::class -> editor.putLong(key, value as Long)
        String::class -> editor.putString(key, value as String)
        else -> {
            if (value is Set<*>) {
                editor.putStringSet(key, value as Set<String>)
            } else {
                editor.putString(key, gson.toJson(value))
            }
        }
    }

    editor.apply()
}

fun String.cleanup(): String {
    runCatching {
        return when {
            this.split(":").isNullOrEmpty() -> this
            this.split(": ").size == 1 -> this
            else -> this.split(
                ": "
            )[1].trim()
        }
    }.getOrElse {
        return ""
    }
}