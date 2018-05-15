package com.speshiou.android.common.ui.widget

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.speshiou.android.common.R
import java.util.*

/**
 * Created by joey on 2017/12/27.
 */
abstract class EnhancedRecyclerAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mHasMoreData = false
    private var mLoadMoreBundle: Bundle? = null
    protected var mData = ArrayList<T>()
    var onItemClickListener: ((view: View, obj: T) -> Unit)? = null
    protected val mOnClickListener = View.OnClickListener { view ->
        onItemClickListener?.invoke(view, view.tag as T)
    }

    var onLoadMoreListener: ((data: Bundle?) -> Unit)? = null

    private var mRecyclerView: RecyclerView? = null
    var emptyView: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_LOAD_MORE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_more, parent, false)
            return object: RecyclerView.ViewHolder(view) {}
        } else {
            return onCreateDataViewHolder(parent, viewType)
        }
    }

    abstract fun onCreateDataViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_LOAD_MORE) {
            holder.itemView.post {
                onLoadMoreListener?.invoke(mLoadMoreBundle)
            }
        } else {
            holder.itemView.tag = mData[position]
            holder.itemView.setOnClickListener(mOnClickListener)
            onBindDataViewHolder(holder, position)
        }
    }

    abstract fun onBindDataViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    override fun getItemCount(): Int {
        return mData.size + if (mHasMoreData) 1 else 0
    }

    open fun addData(data: Array<T>, hasMoreData: Boolean, loadMoreBundle: Bundle?) {
        mHasMoreData = hasMoreData
        mLoadMoreBundle = loadMoreBundle
        mData.addAll(Arrays.asList(*data))
        notifyDataSetChanged()
    }

    open fun updateData(data: Array<T>, hasMoreData: Boolean, loadMoreBundle: Bundle?) {
        mData.clear()
        addData(data, hasMoreData, loadMoreBundle)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1 && mHasMoreData) {
            return VIEW_TYPE_LOAD_MORE
        } else {
            return getDataViewType(position)
        }
    }

    open fun getDataViewType(position: Int): Int {
        return VIEW_TYPE_DATA
    }

    companion object {
        const val VIEW_TYPE_LOAD_MORE = -1
        const val VIEW_TYPE_DATA = -2
    }
}