package com.speshiou.android.common.ui.widget

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.speshiou.android.common.R
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by joey on 2017/12/27.
 */
abstract class EnhancedRecyclerAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var mData = ArrayList<T>()
    private var mHasMoreData = false
    private var mLoadMoreBundle: Bundle? = null
    private var mLoadingMore = false

    var selectionEnabled = true

    var selectedItem: Int by Delegates.observable(-1) {
        property, oldValue, newValue ->
        if (oldValue in 0..(itemCount - 1)) {
            notifyItemChanged(oldValue)
        }
        if (newValue in 0..(itemCount - 1)) {
            notifyItemChanged(newValue)
        }
    }

    var selectedItemData: T? = null
        get() {
            if (selectedItem in 0..(itemCount - 1)) {
                return mData[selectedItem]
            }
            return null
        }

    var onItemClickListener: ((view: View, obj: T) -> Unit)? = null
    protected val mOnClickListener = View.OnClickListener { view ->
        if (selectionEnabled) {
            selectedItem = mData.indexOf(view.tag)
        }
        onItemClickListener?.invoke(view, view.tag as T)
    }

    var onLoadMoreListener: ((data: Bundle?) -> Unit)? = null

    private var mRecyclerView: RecyclerView? = null
    var emptyView by Delegates.observable<View?>(null) {
        property, oldValue, newValue ->

        if (newValue != null) {
            registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    if (itemCount > 0) {
                        newValue?.visibility = View.GONE
                        mRecyclerView?.visibility = View.VISIBLE
                    } else {
                        mRecyclerView?.visibility = View.GONE
                        newValue?.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mRecyclerView = null
    }

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
            if (mHasMoreData && !mLoadingMore) {
                mLoadingMore = true
                holder.itemView.post {
                    onLoadMoreListener?.invoke(mLoadMoreBundle)
                }
            }
        } else {
            holder.itemView.isSelected = position == selectedItem
            holder.itemView.tag = mData[position]
            holder.itemView.setOnClickListener(mOnClickListener)
            onBindDataViewHolder(holder, position)

            if (mHasMoreData && !mLoadingMore && itemCount - position < PRELOAD_MORE_AHEAD) {
                mLoadingMore = true
                holder.itemView.post {
                    onLoadMoreListener?.invoke(mLoadMoreBundle)
                }
            }
        }
    }

    abstract fun onBindDataViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    fun getItem(position: Int): T? {
        if (position < mData.size) {
            return mData[position]
        }
        return null
    }

    override fun getItemCount(): Int {
        return mData.size + if (mHasMoreData) 1 else 0
    }

    open fun indexOfData(obj: T): Int {
        return mData.indexOf(obj)
    }

    open fun addData(data: Array<T>, hasMoreData: Boolean, loadMoreBundle: Bundle?) {
        mHasMoreData = hasMoreData
        mLoadingMore = false
        mLoadMoreBundle = loadMoreBundle
        mData.addAll(Arrays.asList(*data))
        notifyDataSetChanged()
    }

    open fun updateData(data: Array<T>, hasMoreData: Boolean, loadMoreBundle: Bundle?) {
        mData.clear()
        selectedItem = -1
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
        const val PRELOAD_MORE_AHEAD = 30
    }
}