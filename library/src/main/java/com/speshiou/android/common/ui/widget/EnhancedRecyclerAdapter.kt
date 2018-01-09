package com.speshiou.android.common.ui.widget

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

    protected var mData = ArrayList<T>()
    private var mHeaderViewHolder: RecyclerView.ViewHolder? = null
    private var mHasMoreData = false
    private var mLoadingMoreData = false
    private var mLoadMoreBundle: Bundle? = null
    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    private var mEmptyViewCallback: EmptyViewCallbacks? = null
    var onItemClickListener: ((view: View, obj: T) -> Unit)? = null
    protected val mOnClickListener = View.OnClickListener { view ->
        onItemClickListener?.invoke(view, view.tag as T)
    }

    val dataCount: Int
        get() = mData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        if (viewType == VIEW_TYPE_HEADER) {
            return mHeaderViewHolder
        } else if (viewType == VIEW_TYPE_MORE) {
            val viewMore = LayoutInflater.from(parent.context).inflate(R.layout.list_item_more, parent, false)
            return MoreViewHolder(viewMore)
        } else return if (viewType == VIEW_TYPE_EMPTY_VIEW) {
            mEmptyViewCallback!!.onBindEmptyViewHolder(parent)
        } else {
            onCreateDataViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_HEADER) {
            //            return mHeaderView;
        } else if (viewType == VIEW_TYPE_EMPTY_VIEW) {
            mEmptyViewCallback!!.onBindDataViewHolder(holder)
        } else if (viewType == VIEW_TYPE_MORE) {
            if (!mLoadingMoreData) {
                mLoadingMoreData = true
                holder.itemView.post {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener!!.onLoadMore(mLoadMoreBundle)
                    }
                }
            }
        } else {
            onBindDataViewHolder(holder, getDataPosition(position))
        }
    }

    abstract fun onCreateDataViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun onBindDataViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    open fun getDataItemViewType(dataPosition: Int): Int {
        return VIEW_TYPE_DATA
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasHeader() && position == 0) {
            VIEW_TYPE_HEADER
        } else if (mData.size == 0 && !mHasMoreData && mEmptyViewCallback != null
                && position == itemCount - 1 && position == 0 + if (hasHeader()) 1 else 0) {
            VIEW_TYPE_EMPTY_VIEW
        } else if (mHasMoreData && position == itemCount - 1) {
            VIEW_TYPE_MORE
        } else {
            getDataItemViewType(getDataPosition(position))
        }
    }

    override fun getItemCount(): Int {
        return if (mData.size > 0 || mData.size == 0 && mHasMoreData) {
            mData.size + (if (hasHeader()) 1 else 0) + if (mHasMoreData) 1 else 0
        } else {
            (if (hasHeader()) 1 else 0) + if (mEmptyViewCallback == null) 0 else 1
        }
    }

    fun getData(dataPosition: Int): T {
        return mData[dataPosition]
    }

    private fun getDataPosition(position: Int): Int {
        return if (hasHeader()) position - 1 else position
    }

    fun setHeaderViewHolder(viewHolder: RecyclerView.ViewHolder) {
        mHeaderViewHolder = viewHolder
        notifyDataSetChanged()
    }

    fun hasHeader(): Boolean {
        return mHeaderViewHolder != null
    }

    open fun addData(data: Array<T>?, moreData: Boolean, loadMoreBundle: Bundle?) {
        mHasMoreData = moreData
        mLoadMoreBundle = loadMoreBundle
        mLoadingMoreData = false
        if (data != null) {
            mData.addAll(Arrays.asList(*data))
            notifyDataSetChanged()
        }
    }

    open fun updateData(data: Array<T>, moreData: Boolean, loadMoreBundle: Bundle?) {
        mData.clear()
        addData(data, moreData, loadMoreBundle)
    }

    fun setHasMoreData(moreData: Boolean) {
        mHasMoreData = moreData
        notifyDataSetChanged()
    }

    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        mOnLoadMoreListener = listener
    }

    fun setEmptyViewCallback(callback: EmptyViewCallbacks) {
        mEmptyViewCallback = callback
        notifyDataSetChanged()
    }

    interface OnLoadMoreListener {
        fun onLoadMore(loadMoreBundle: Bundle?)
    }

    internal class MoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface EmptyViewCallbacks {
        fun onBindEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
        fun onBindDataViewHolder(holder: RecyclerView.ViewHolder)
    }

    companion object {

        private val VIEW_TYPE_HEADER = 0
        private val VIEW_TYPE_DATA = 1
        private val VIEW_TYPE_EMPTY_VIEW = 2
        private val VIEW_TYPE_MORE = 3
        private val VIEW_TYPE_COUNT = 4

        val VIEW_TYPE_DATA_BASE = 100
    }
}