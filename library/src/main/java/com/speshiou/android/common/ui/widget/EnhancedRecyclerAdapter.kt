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
    var onItemClickListener: ((view: View, obj: T) -> Unit)? = null
    protected val mOnClickListener = View.OnClickListener { view ->
        onItemClickListener?.invoke(view, view.tag as T)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tag = mData[position]
        holder.itemView.setOnClickListener(mOnClickListener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    open fun addData(data: Array<T>) {
        mData.addAll(Arrays.asList(*data))
        notifyDataSetChanged()
    }

    open fun updateData(data: Array<T>) {
        mData.clear()
        addData(data)
    }

    internal class MoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface EmptyViewCallbacks {
        fun onBindEmptyViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
        fun onBindDataViewHolder(holder: RecyclerView.ViewHolder)
    }
}