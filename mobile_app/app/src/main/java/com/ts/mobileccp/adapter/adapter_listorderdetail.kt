package com.ts.mobileccp.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R
import com.ts.mobileccp.db.entity.SalesOrder


class ListOrderDetailAdapter(private var mList: List<SalesOrder>) : RecyclerView.Adapter<ListOrderDetailAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_listorderdetail_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]


        holder.txtOrderNo.text = ItemsViewModel.orderno
        holder.txtOrderDetail.text = "ItemsViewModel.address"
        holder.txtID.text = ItemsViewModel.id.toString()
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtID: TextView = itemView.findViewById(R.id.txtID)
        val txtOrderNo: TextView = itemView.findViewById(R.id.txtOrderNo)
        val txtOrderDetail: TextView = itemView.findViewById(R.id.txtOrderDetail)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<SalesOrder>) {
        mList = newList
        notifyDataSetChanged()
    }
}
