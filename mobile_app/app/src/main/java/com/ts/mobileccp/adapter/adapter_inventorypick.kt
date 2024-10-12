package com.ts.mobileccp.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R
import com.ts.mobileccp.db.entity.InventoryLookup
import com.ts.mobileccp.db.entity.TmpSalesOrderItem
import java.text.NumberFormat


class InventoryPickAdapter(
    private var mList: List<InventoryLookup>,
    private var soItemList: MutableList<TmpSalesOrderItem>, //this is trial, let me update from fragment
    private val listener: ProductPickListener
) : RecyclerView.Adapter<InventoryPickAdapter.ViewHolder>() {

    val format = NumberFormat.getNumberInstance()

    init {
        format.maximumFractionDigits = 0
        format.minimumFractionDigits = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_inventorypick_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.txtProductName.text = item.description
        holder.txtSKU.text = item.partno
        holder.txtCategory.text = item.pclass8name

        val price = item.getPrice( format)
        holder.txtPrice.text = price

        updateDataHolder(soItemList, holder, item)

        holder.btnAdd.setOnClickListener{
            listener.onUpdateQty(item, position, 1, 1)
        }

        holder.btnMin.setOnClickListener{
            listener.onUpdateQty(item, position, 1, -1)
        }


    }


    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProductName: TextView = itemView.findViewById(R.id.txtProductName)
        val txtSKU: TextView = itemView.findViewById(R.id.txtSKU)
        val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val txtEdQty: EditText = itemView.findViewById(R.id.txtEdQty)
        val btnAdd: ImageButton = itemView.findViewById(R.id.btnAdd)
        val btnMin: ImageButton = itemView.findViewById(R.id.btnMin)
        val cardView: CardView = itemView.findViewById(R.id.cardView)

    }

//    fun filter(query: String) {
//        filteredItems = if (query.isEmpty()) {
//            mList
//        } else {
//            mList.filter {
//                it.nama.contains(query, ignoreCase = true) ||
//                        it.merk.contains(query, ignoreCase = true)
//            }
//        }
//        notifyDataSetChanged()
//    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<InventoryLookup>) {
        mList = newList
//        filteredItems = mList
        notifyDataSetChanged()
    }

    fun updateDataHolder(tmpSOItems: List<TmpSalesOrderItem>, holder: ViewHolder, product: InventoryLookup){
        fun getSOItem(partno:String): TmpSalesOrderItem? {
            for(tmp in tmpSOItems){
                if ( tmp.partno.equals(partno) ){
                    return tmp
                }
            }
            return null
        }
        val qty = getSOItem(product.partno)?.qty?:0
        if (qty > 0) {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.highlight)
            )
        }else{
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.white)
            )
        }
        holder.txtEdQty.setText( qty.toString() )

    }



}

interface ProductPickListener {
    fun onUpdateQty(prod: InventoryLookup, position: Int, qtyIndex: Int, increment:Int)
}