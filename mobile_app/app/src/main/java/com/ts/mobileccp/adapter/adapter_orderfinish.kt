package com.ts.mobileccp.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R
import com.ts.mobileccp.db.entity.TmpSalesOrderItem
import java.text.NumberFormat


class OrderFinishAdapter(
    private var soItemList: MutableList<TmpSalesOrderItem>, //this is trial, let me update from fragment
    private val listener: OrderFinishListener
) : RecyclerView.Adapter<OrderFinishAdapter.ViewHolder>() {

    val format = NumberFormat.getNumberInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_listorderfinish_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = soItemList[position]

        format.maximumFractionDigits = 0
        format.minimumFractionDigits = 0

        holder.txtProduct.text = item.invname

        val uomPrice = " eks/Rp" + format.format(item.price)
        holder.txtUOM.text = uomPrice
        holder.txtEdQty.setText(item.qty.toString())

        holder.btnAdd.setOnClickListener{
            listener.onUpdateQty(item, position,  1)
        }

        holder.btnMin.setOnClickListener{
            listener.onUpdateQty(item, position,  -1)
        }

    }


    override fun getItemCount(): Int {
        return soItemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProduct: TextView = itemView.findViewById(R.id.txtProduct)
        val txtUOM: TextView = itemView.findViewById(R.id.txtUOM)
        val txtEdQty: EditText = itemView.findViewById(R.id.txtEdQty)
        val btnAdd: ImageButton = itemView.findViewById(R.id.btnAdd)
        val btnMin: ImageButton = itemView.findViewById(R.id.btnMin)
    }

}



interface OrderFinishListener {
    fun onUpdateQty(soItem: TmpSalesOrderItem, position: Int,  increment:Int)
}
