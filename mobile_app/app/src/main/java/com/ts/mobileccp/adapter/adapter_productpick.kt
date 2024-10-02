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
import com.ts.mobileccp.db.entity.ProductLookup
import com.ts.mobileccp.db.entity.TmpSalesOrderItem
import java.text.NumberFormat


class ProductPickAdapter(
    private var mList: List<ProductLookup>,
    private var soItemList: MutableList<TmpSalesOrderItem>, //this is trial, let me update from fragment
    private var isTradUOM: Boolean,
    private val listener: ProductPickListener
) : RecyclerView.Adapter<ProductPickAdapter.ViewHolder>() {

    val format = NumberFormat.getNumberInstance()

    init {
        format.maximumFractionDigits = 0
        format.minimumFractionDigits = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_productpick_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]

        holder.txtProductName.text = item.nama
        holder.txtSKU.text = item.sku

        val desc = item.principal + " - " + item.merk
        holder.txtDesc.text = desc

        val price1 = item.getUOMDesc(1, isTradUOM, format)
        val price2 = item.getUOMDesc(2, isTradUOM, format)
        val price3 = item.getUOMDesc(3, isTradUOM, format)

        holder.txtPrice.text = price1
        holder.txtUOM1Price.text = price1
        holder.txtUOM2Price.text = price2
        holder.txtUOM3Price.text = price3

        holder.txtUOM1.text = item.getUOM(1, isTradUOM)
        holder.txtUOM2.text = item.getUOM(2, isTradUOM)
        holder.txtUOM3.text = item.getUOM(3, isTradUOM)

        updateDataHolder(soItemList, holder, item)

        holder.cardView.setOnClickListener{
            listener.onExpand(item, position)
        }

//        holder.btnExpand.setOnClickListener{
//            listener.onExpand(item, position)
//        }

        holder.btnAdd1.setOnClickListener{
            listener.onUpdateQty(item, position, 1, 1)
        }
        holder.btnAdd2.setOnClickListener{
            listener.onUpdateQty(item, position, 2, 1)
        }
        holder.btnAdd3.setOnClickListener{
            listener.onUpdateQty(item, position, 3, 1)
        }

        holder.btnMin1.setOnClickListener{
            listener.onUpdateQty(item, position, 1, -1)
        }
        holder.btnMin2.setOnClickListener{
            listener.onUpdateQty(item, position, 2, -1)
        }
        holder.btnMin3.setOnClickListener{
            listener.onUpdateQty(item, position, 3, -1)
        }

    }


    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProductName: TextView = itemView.findViewById(R.id.txtProductName)
        val txtSKU: TextView = itemView.findViewById(R.id.txtSKU)
        val txtDesc: TextView = itemView.findViewById(R.id.txtDesc)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)

        val txtUOM1: TextView = itemView.findViewById(R.id.txtUOM1)
        val txtUOM2: TextView = itemView.findViewById(R.id.txtUOM2)
        val txtUOM3: TextView = itemView.findViewById(R.id.txtUOM3)

        val txtUOM1Price: TextView = itemView.findViewById(R.id.txtUOM1Price)
        val txtUOM2Price: TextView = itemView.findViewById(R.id.txtUOM2Price)
        val txtUOM3Price: TextView = itemView.findViewById(R.id.txtUOM3Price)

        val txtQty1: TextView = itemView.findViewById(R.id.txtQty1)
        val txtQty2: TextView = itemView.findViewById(R.id.txtQty2)
        val txtQty3: TextView = itemView.findViewById(R.id.txtQty3)

        val txtEdQty1: EditText = itemView.findViewById(R.id.txtEdQty1)
        val txtEdQty2: EditText = itemView.findViewById(R.id.txtEdQty2)
        val txtEdQty3: EditText = itemView.findViewById(R.id.txtEdQty3)

        val btnExpand: ImageButton = itemView.findViewById(R.id.btnExpand)
        val lnEditQty: LinearLayout = itemView.findViewById(R.id.lnEditQty)

        val btnAdd1: ImageButton = itemView.findViewById(R.id.btnAdd1)
        val btnAdd2: ImageButton = itemView.findViewById(R.id.btnAdd2)
        val btnAdd3: ImageButton = itemView.findViewById(R.id.btnAdd3)

        val btnMin1: ImageButton = itemView.findViewById(R.id.btnMin1)
        val btnMin2: ImageButton = itemView.findViewById(R.id.btnMin2)
        val btnMin3: ImageButton = itemView.findViewById(R.id.btnMin3)
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
    fun updateData(newList: List<ProductLookup>) {
        mList = newList
//        filteredItems = mList
        notifyDataSetChanged()
    }

    fun updateDataHolder(tmpSOItems: List<TmpSalesOrderItem>, holder: ViewHolder, product: ProductLookup){
        fun getSOItem(sku:String, idx:Int): TmpSalesOrderItem? {
            var uom : String? = product.getUOM(idx, isTradUOM)
//            when (idx) {
//                1 -> uom = product.uom_1
//                2 -> uom = product.uom_2
//                3 -> uom = product.uom_3
//            }
            for(tmp in tmpSOItems){
                if ( tmp.sku.equals(sku) && tmp.uom.equals(uom)){
                    return tmp
                }
            }
            return null
        }

        val qty1 = getSOItem(product.sku, 1)?.qty?:0
        val qty2 = getSOItem(product.sku, 2)?.qty?:0
        val qty3 = getSOItem(product.sku, 3)?.qty?:0

        val totalQty = qty1 + qty2 + qty3;

        if (totalQty > 0) {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.highlight)
            )
        }else{
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.white)
            )
        }

        holder.txtEdQty1.setText( qty1.toString() )
        holder.txtEdQty2.setText( qty2.toString() )
        holder.txtEdQty3.setText( qty3.toString() )

        holder.txtQty1.text = holder.txtEdQty1.text
        holder.txtQty2.text = holder.txtEdQty2.text
        holder.txtQty3.text = holder.txtEdQty3.text


        if (product.expanded){
            holder.lnEditQty.visibility = View.VISIBLE
            holder.btnExpand.setImageResource(R.drawable.ic_expand_less_dark)
        }else{
            holder.lnEditQty.visibility = View.GONE
            holder.btnExpand.setImageResource(R.drawable.ic_expand_more_dark)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTraditionalUOM(isTrad: Boolean){
        isTradUOM = isTrad
        notifyDataSetChanged()
    }

}

interface ProductPickListener {
    fun onUpdateQty(prod: ProductLookup, position: Int, qtyIndex: Int, increment:Int)
    fun onExpand(prod: ProductLookup, position: Int)
}