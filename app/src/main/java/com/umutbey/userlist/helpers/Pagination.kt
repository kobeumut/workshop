package com.umutbey.userlist.helpers

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class Pagination(layoutManager: RecyclerView.LayoutManager) :
    RecyclerView.OnScrollListener() {
    private val layoutManager: androidx.recyclerview.widget.LinearLayoutManager =
        layoutManager as androidx.recyclerview.widget.LinearLayoutManager
    private val visibleThreshold = 10
    private var currentPage = 0
    private var previousTotalItemCount = 0
    private var loading = true
    private val startingPageIndex = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        val totalItemCount = layoutManager.itemCount
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }

        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, recyclerView)
            loading = true
        }
    }

    abstract fun onLoadMore(currentPage: Int, totalItemCount: Int, view: View)
}
