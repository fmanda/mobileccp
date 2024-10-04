package com.ts.mobileccp.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R
import com.ts.mobileccp.db.entity.PriceLevel
import java.text.NumberFormat


class PriceLevelAdapter(
    private var mList: List<PriceLevel>,
) : RecyclerView.Adapter<PriceLevelAdapter.ViewHolder>() {

    val format: NumberFormat = NumberFormat.getNumberInstance()

    init {
        format.maximumFractionDigits = 0
        format.minimumFractionDigits = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_pricelevel_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.txtPriceLevel.text = item.pricelevelname
        val price = "Rp " + format.format(item.price)
        holder.txtPrice.text = price
    }


    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPriceLevel: TextView = itemView.findViewById(R.id.txtPriceLevel)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
    }




}
