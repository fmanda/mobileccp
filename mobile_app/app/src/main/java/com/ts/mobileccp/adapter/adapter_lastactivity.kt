package com.ts.mobileccp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R
import com.ts.mobileccp.db.entity.LastActivityQuery
import java.text.DecimalFormat
import java.text.NumberFormat


class LastActivityAdapter(private var mList: List<LastActivityQuery>) : RecyclerView.Adapter<LastActivityAdapter.ViewHolder>() {
    private val formatter: NumberFormat = DecimalFormat("Rp #,###")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_lastactivity_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val soItem = mList[position]

        holder.txtDate.text = soItem.orderdate
        holder.txtCustomer.text = soItem.customer
        holder.txtAddress.text = soItem.alamat
        holder.txtAmount.text = soItem.amt?.let {formatter.format(it)}

        val location:String = soItem.latitude.toString() + "," + soItem.longitude.toString()
        holder.txtLocation.text = location
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtCustomer: TextView = itemView.findViewById(R.id.txtCustomer)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)
        val txtLocation: TextView = itemView.findViewById(R.id.txtLocation)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<LastActivityQuery>) {
        mList = newList
        notifyDataSetChanged()
    }
}
