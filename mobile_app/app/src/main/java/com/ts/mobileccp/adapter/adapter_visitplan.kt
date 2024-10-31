package com.ts.mobileccp.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R
import com.ts.mobileccp.db.entity.LastVisitPlan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class VisitPlanAdapter(var mList: List<LastVisitPlan>, var mlistener: VisitPlanListener) : RecyclerView.Adapter<VisitPlanAdapter.ViewHolder>() {

    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val listener = mlistener

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCustomer: TextView = itemView.findViewById(R.id.txtCustomer)
        val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtDay: TextView = itemView.findViewById(R.id.txtDay)
        val lnAction: LinearLayout = itemView.findViewById(R.id.lnAction)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_listvisitplan_layout, parent, false)

        return ViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.txtCustomer.text = item.partnername
        holder.txtAddress.text = item.partneraddress


        val date: Date? = inputFormat.parse(item.datetr)

        date?.let {
            holder.txtDay.text = dayFormat.format(it)
            holder.txtDate.text = dateFormat.format(it)
        }


        holder.lnAction.setOnClickListener() {
            listener.onClickListener(item, position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<LastVisitPlan>) {
        mList = newList
        notifyDataSetChanged()
    }
}

interface VisitPlanListener{
    fun onClickListener(visitplan: LastVisitPlan, position: Int)
}