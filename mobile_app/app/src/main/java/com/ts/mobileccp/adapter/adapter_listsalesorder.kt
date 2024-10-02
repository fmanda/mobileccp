package com.ts.mobileccp.adapter

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R
import com.ts.mobileccp.db.entity.LastActivityQuery
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale


class ListSalesOrderAdapter(private var mList: List<LastActivityQuery>, val listener: ListSalesOrderListener, val isDashBoard:Boolean = false) : RecyclerView.Adapter<ListSalesOrderAdapter.ViewHolder>() {
    private val formatter: NumberFormat = DecimalFormat("Rp #,###")
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_listsalesorder_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val soItem = mList[position]

        val date: Date? = inputFormat.parse(soItem.orderdate)
        val formattedDate = date?.let { outputFormat.format(it) }

        holder.txtDate.text = formattedDate
        holder.txtCustomer.text = soItem.customer
        holder.txtAddress.text = soItem.alamat
        holder.txtAmount.text = soItem.amt?.let {formatter.format(it)}

        val location:String = soItem.latitude.toString() + "," + soItem.longitude.toString()
        holder.txtLocation.text = location

        if (soItem.isexpanded) {
            holder.lnOperation.visibility = View.VISIBLE
            holder.btnExpand.setImageResource(R.drawable.ic_expand_less_dark)
        }else{
            holder.lnOperation.visibility = View.GONE
            holder.btnExpand.setImageResource(R.drawable.ic_expand_more_dark)
        }

        holder.lnBody.setOnClickListener(){
            listener.onClick(soItem, position)
        }

        holder.btnEdit.setOnClickListener(){
            listener.onEdit(soItem, position)
        }

        holder.btnUpload.setOnClickListener(){
            listener.onUpload(soItem, position)
        }

        holder.txtOrderNo.text = soItem.orderno

        if (soItem.uploaded == 1){
            holder.txtUpload.text = "Uploaded"
            holder.txtUpload.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.teal_700))
            holder.btnUpload.text = "Re-Upload"
        }else{
            holder.txtUpload.text = "Belum Upload"
            holder.txtUpload.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.secondary))
            holder.btnUpload.text = "Upload"
        }

        if (isDashBoard){
            holder.btnExpand.visibility = View.GONE
        }else{
            holder.btnExpand.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtCustomer: TextView = itemView.findViewById(R.id.txtCustomer)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)
        val txtLocation: TextView = itemView.findViewById(R.id.txtLocation)
        val lnBody: LinearLayout = itemView.findViewById(R.id.lnBody)
        val lnOperation: LinearLayout = itemView.findViewById(R.id.lnOperation)
        val txtUpload: TextView = itemView.findViewById(R.id.txtUpload)
        val txtOrderNo: TextView = itemView.findViewById(R.id.txtOrderNo)
        val btnUpload : Button = itemView.findViewById(R.id.btnUpload)
        val btnEdit : Button = itemView.findViewById(R.id.btnEdit)
        val btnExpand: ImageButton = itemView.findViewById(R.id.btnExpand)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<LastActivityQuery>) {
        mList = newList
        notifyDataSetChanged()
    }
}


interface ListSalesOrderListener {
    fun onClick(activity: LastActivityQuery, position: Int)
    fun onEdit(activity: LastActivityQuery, position: Int)
    fun onUpload(activity: LastActivityQuery, position: Int)

}