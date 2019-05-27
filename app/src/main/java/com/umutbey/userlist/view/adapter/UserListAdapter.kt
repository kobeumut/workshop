package com.umutbey.userlist.view.adapter

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.umutbey.userlist.ApplicationWrapper
import com.umutbey.userlist.ImageModule
import com.umutbey.userlist.R
import com.umutbey.userlist.helpers.get
import com.umutbey.userlist.helpers.getObject
import com.umutbey.userlist.helpers.put
import com.umutbey.userlist.model.User
import com.umutbey.userlist.view.MainActivity
import com.umutbey.userlist.view.ProfileActivity
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import java.util.*


class UserListAdapter(private val mCtx: Context, var listType: Int) :
    RecyclerView.Adapter<UserListAdapter.ItemViewHolder>(), KoinComponent {
    private val imageModule: ImageModule by inject { parametersOf(mCtx) }
    private val pref:SharedPreferences by lazy { ApplicationWrapper.INSTANCE.getSharedPreferences(MainActivity.PREFS_FILENAME,0) }

    private var items: MutableList<User> = ArrayList()
    fun addItems(items: List<User>) {
        this.items.addAll(items)
        notifyDataSetChanged()
        Log.d("SIZE", "${this.items.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View? = if (listType != 0) LayoutInflater.from(mCtx).inflate(R.layout.user_grid, parent, false)
        else LayoutInflater.from(mCtx).inflate(R.layout.user_item, parent, false)
        return ItemViewHolder(view!!)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        ValueAnimator.ofInt(Color.TRANSPARENT, Color.parseColor(item.favoriteColor))
            .apply {
                setEvaluator(ArgbEvaluator())
                addUpdateListener {
                    val value = it.animatedValue as Int
                    holder.userLayout.setBackgroundColor(value)
                    interpolator = AccelerateInterpolator(1.5f)
                    duration = 1500
                }
            }.start()
        holder.textView.text = "${item.name} ${item.surname}"
        imageModule.get(item.avatar, holder.imageView, isCircle = listType != 0)
//        holder.userLayout.setOnLongClickListener {
//            pref.put(item.id.toString(),item)
//            true
//        }
        holder.userLayout.setOnClickListener {
            val savedUser = pref.getObject<User>(item.id.toString())
            mCtx.startActivity(
                Intent(mCtx, ProfileActivity::class.java)
                    .putExtra("DATA", if(savedUser?.id == null)item else savedUser)
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getLastVisibleItemId(): Int {
        return if (items.isEmpty()) {
            0
        } else items[items.size - 1].id
    }

    fun getList(): MutableList<User> {
        return items
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textView: TextView = itemView.findViewById(R.id.profile_name)
        var imageView: ImageView = itemView.findViewById(R.id.profile_imageview)
        var userLayout: RelativeLayout = itemView.findViewById(R.id.profie_layout)

    }

}
