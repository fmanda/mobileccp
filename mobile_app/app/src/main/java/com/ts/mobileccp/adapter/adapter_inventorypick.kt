package com.ts.mobileccp.adapter


import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
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
        holder.txtEdQty.removeTextChangedListener(holder.txtEdQty.tag as? TextWatcher)

        val item = mList[position]
        holder.txtProductName.text = item.description
        holder.txtSKU.text = item.partno
        holder.txtCategory.text = item.pclass8name

        val price = item.getPrice( format)
        holder.txtPrice.text = price

        updateDataHolder(soItemList, holder, item)

        holder.btnAdd.setOnClickListener{
            listener.onUpdateQty(item, position,1)
        }

        holder.btnMin.setOnClickListener{
            listener.onUpdateQty(item, position, -1)
        }


        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                val number: Int? = input.toIntOrNull()


                listener.onQuantityChanged(item, holder.adapterPosition, number?:0)

                if (holder.txtEdQty.text.toString() != input){
                    holder.txtEdQty.setText(input)
                    holder.txtEdQty.setSelection(holder.txtEdQty.text.length)
                }

            }
        }

        holder.txtEdQty.addTextChangedListener(textWatcher)

        // Save the TextWatcher reference in the tag
        holder.txtEdQty.tag = textWatcher

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

        if (holder.txtEdQty.text.toString() != qty.toString()){
            holder.txtEdQty.setText( qty.toString() )
            holder.txtEdQty.setSelection(holder.txtEdQty.text.length)
        }


    }



}

interface ProductPickListener {
    fun onUpdateQty(prod: InventoryLookup, position: Int, increment:Int)
    fun onQuantityChanged(prod: InventoryLookup,  position: Int, newQuantity: Int)
}