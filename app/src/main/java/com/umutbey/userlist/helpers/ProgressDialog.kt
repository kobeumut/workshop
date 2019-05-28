package com.umutbey.userlist.helpers

import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import org.jetbrains.anko.*

class ProgressDialog(private val context: Context) {
    private var dialog:DialogInterface? = null
    fun show(){
        dialog = context.alert {
            customView {
                linearLayout {
                    gravity = Gravity.CENTER_VERTICAL
                    progressBar {
                        padding = dip(16)
                    }
                    textView {
                        text = "Please waiting..."
                        padding = dip(16)
                    }
                }
            }
        }.show()
    }
    fun hide(){
        dialog?.dismiss()
    }
}