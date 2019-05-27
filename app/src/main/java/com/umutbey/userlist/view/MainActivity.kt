package com.umutbey.userlist.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.umutbey.userlist.ApplicationWrapper
import com.umutbey.userlist.R
import com.umutbey.userlist.helpers.Pagination
import com.umutbey.userlist.helpers.get
import com.umutbey.userlist.helpers.put
import com.umutbey.userlist.network.ApiInterface
import com.umutbey.userlist.view.adapter.UserListAdapter
import com.umutbey.userlist.viewmodel.LoginViewModel
import com.umutbey.userlist.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {
    private val apiInterface: ApiInterface by inject<ApiInterface>()
    private val viewModel: MainViewModel by viewModel{ parametersOf(apiInterface)}

    companion object {
        const val LIST = 0
        const val GRID = 1
        const val PREFS_FILENAME = "advancedUserList"
        const val LIST_TYPE = "listType"
        const val PAGE_SIZE = 20
    }

    private lateinit var prefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        prefs =  ApplicationWrapper.INSTANCE.getSharedPreferences(PREFS_FILENAME, 0)


        toggleList(prefs.get(LIST_TYPE, 0))
    }


    private fun toggleList(type: Int) {
        prefs.put(LIST_TYPE, type)
        if (type == LIST) recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this) else
            recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)

        loadData(recyclerView.layoutManager!!, type)
    }

    private fun loadData(layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager, type: Int) {
        recyclerView!!.layoutManager = layoutManager
        val mAdapter = UserListAdapter(this, type)
        recyclerView!!.adapter = mAdapter
        viewModel.user!!.observe(this, Observer {
            if (it != null) {
                mAdapter.addItems(it)
            }
        })

        recyclerView!!.addOnScrollListener(object : Pagination(recyclerView.layoutManager!!) {
            override fun onLoadMore(currentPage: Int, totalItemCount: Int, view: View) {
                viewModel.loadMore(currentPage * PAGE_SIZE)
            }
        })

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_manu, menu)
        updateMenu(menu!!.findItem(R.id.action_list))
        return true
    }

    private fun updateMenu(item: MenuItem) =
        if (prefs.get<Int>(LIST_TYPE, 0) == 0) item.icon =
            ContextCompat.getDrawable(this, R.drawable.grid_icon)
        else item.icon = ContextCompat.getDrawable(this, R.drawable.list_icon)

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_list -> {
                if (prefs.get<Int>(LIST_TYPE, 0) != 0) {
                    item.icon = ContextCompat.getDrawable(this, R.drawable.grid_icon)
                    toggleList(LIST)
                } else {
                    item.icon = ContextCompat.getDrawable(this, R.drawable.list_icon)
                    toggleList(GRID)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


}