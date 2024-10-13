package com.ts.mobileccp.adapter

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R
import com.ts.mobileccp.db.entity.ARInv
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale


class ListARInvAdapter(private var mList: List<ARInv>) : RecyclerView.Adapter<ListARInvAdapter.ViewHolder>() {
    private val formatter: NumberFormat = DecimalFormat("Rp #,###")
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outputFormatDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_arinv_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val arinv = mList[position]

        val date: Date? = inputFormat.parse(arinv.invdate)
        val formattedDate = date?.let { outputFormatDate.format(it) }

        holder.txtInvDate.text = formattedDate
        holder.txtCustomer.text = arinv.partnername
        holder.txtInvNo.text = arinv.invno
        holder.txtInvDate.text = arinv.invdate
        holder.txtAmount.text = formatter.format(arinv.amount)
        holder.txtRemain.text = formatter.format(arinv.remain)
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtInvNo: TextView = itemView.findViewById(R.id.txtInvNo)
        val txtInvDate: TextView = itemView.findViewById(R.id.txtInvDate)
        val txtCustomer: TextView = itemView.findViewById(R.id.txtCustomer)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtRemain: TextView = itemView.findViewById(R.id.txtRemain)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<ARInv>) {
        mList = newList
        notifyDataSetChanged()
    }
}

