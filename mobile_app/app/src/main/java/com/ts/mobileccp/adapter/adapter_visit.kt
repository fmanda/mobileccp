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
import com.ts.mobileccp.db.entity.LastVisit
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale


class ListVisitAdapter(private var mList: List<LastVisit>, val listener: ListVisitListener, val isDashBoard:Boolean = false) : RecyclerView.Adapter<ListVisitAdapter.ViewHolder>() {
    private val formatter: NumberFormat = DecimalFormat("Rp #,###")
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputFormatDay = SimpleDateFormat("EEEE", Locale.getDefault())
    private val outputFormatDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val outputFormatTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_listvisit_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val visit = mList[position]

        val date: Date? = inputFormat.parse(visit.visitdate)


        val formattedDay = date?.let { outputFormatDay.format(it) }
        val formattedDate = date?.let { outputFormatDate.format(it) }
        val formattedTime = date?.let { outputFormatTime.format(it) }

        holder.txtDay.text = formattedDay
        holder.txtDate.text = formattedDate
        holder.txtTime.text = formattedTime

        holder.txtCustomer.text = visit.shipname
        holder.txtAddress.text = visit.shipaddress
        holder.txtSCH.text = visit.ccpsch


        val location:String = visit.lat.toString() + "," + visit.lng.toString()
        holder.txtLocation.text = location

        if (!isDashBoard) {
            holder.lnOperation.visibility = View.VISIBLE
        }else{
            holder.lnOperation.visibility = View.GONE
        }

        holder.btnEdit.setOnClickListener(){
            listener.onEdit(visit, position)
        }

        holder.btnUpload.setOnClickListener(){
            listener.onUpload(visit, position)
        }


        if (visit.uploaded == 1){
            holder.txtUpload.text = "Uploaded"
            holder.txtUpload.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.teal_700))
            holder.btnUpload.visibility = View.GONE
            holder.btnUpload.text = "Re-Upload"
        }else{
            holder.txtUpload.text = "Belum Upload"
            holder.txtUpload.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.secondary))
            holder.btnUpload.visibility = View.VISIBLE
            holder.btnUpload.text = "Upload"
        }


        if (!isDashBoard){
            holder.lnBody.setOnClickListener(){
                listener.onClick(visit, position)
            }
        }

        if (visit.isexpanded) {
            holder.lnOperation.visibility = View.VISIBLE
//            holder.btnExpand.setImageResource(R.drawable.ic_expand_less_dark)
        }else{
            holder.lnOperation.visibility = View.GONE
//            holder.btnExpand.setImageResource(R.drawable.ic_expand_more_dark)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtDay: TextView = itemView.findViewById(R.id.txtDay)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtTime: TextView = itemView.findViewById(R.id.txtTime)
        val txtCustomer: TextView = itemView.findViewById(R.id.txtCustomer)

        val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)
        val txtLocation: TextView = itemView.findViewById(R.id.txtLocation)
        val lnBody: LinearLayout = itemView.findViewById(R.id.lnBody)
        val lnOperation: LinearLayout = itemView.findViewById(R.id.lnOperation)
        val txtUpload: TextView = itemView.findViewById(R.id.txtUpload)

        val btnUpload : Button = itemView.findViewById(R.id.btnUpload)
        val btnEdit : Button = itemView.findViewById(R.id.btnEdit)
        val txtSCH: TextView = itemView.findViewById(R.id.txtSCH)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<LastVisit>) {
        mList = newList
        notifyDataSetChanged()
    }
}


interface ListVisitListener {
    fun onClick(visit: LastVisit, position: Int)
    fun onEdit(visit: LastVisit, position: Int)
    fun onUpload(visit: LastVisit, position: Int)

}