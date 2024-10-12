package com.ts.mobileccp.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R
import com.ts.mobileccp.db.entity.Inventory
import com.ts.mobileccp.db.entity.PriceLevel


class InventoryAdapter(
    private var mList: List<Inventory>,
    private var mListPriceLevel: List<PriceLevel>
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_inventory_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.txtProductName.text = item.description
        holder.txtSKU.text = item.partno
        holder.txtCategory.text = item.pclass8name

        holder.rvPriceLevel.layoutManager = LinearLayoutManager(holder.itemView.context)


        val filteredPriceLevel = mListPriceLevel.filter { it.invid == item.invid }
        val priceLevelAdapter = PriceLevelAdapter(filteredPriceLevel)
        holder.rvPriceLevel.adapter = priceLevelAdapter
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProductName: TextView = itemView.findViewById(R.id.txtProductName)
        val txtSKU: TextView = itemView.findViewById(R.id.txtSKU)
        val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
        val rvPriceLevel: RecyclerView = itemView.findViewById(R.id.rvPriceLevel)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataPriceLevel(newList: List<PriceLevel>) {
        mListPriceLevel = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Inventory>) {
        mList = newList
        notifyDataSetChanged()
    }
}
