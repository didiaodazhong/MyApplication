package com.peixing.myapplication.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.peixing.myapplication.bean.FilterData
import com.peixing.myapplication.R

import java.util.ArrayList

/**
 * A custom adapter to use with the RecyclerView widget.
 */
class RecyclerViewAdapter(private val context: Context, private var datas: ArrayList<FilterData>?) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {


    fun setData(TempDatas: ArrayList<FilterData>) {
        this.datas = TempDatas
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler, null)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(itemViewHolder: ViewHolder, position: Int) {

        //Here you can fill your row view
        itemViewHolder.tvItemRcy.text = datas!![position].name
    }

    override fun getItemCount(): Int {
        return if (datas != null) datas!!.size else 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val tvItemRcy: TextView
        init {
            tvItemRcy = itemView.findViewById<View>(R.id.tv_item_rcy) as TextView
        }
    }
}
