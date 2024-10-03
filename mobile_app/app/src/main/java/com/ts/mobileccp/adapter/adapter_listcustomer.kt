package com.ts.mobileccp.adapter


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.net.Uri
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R
import com.ts.mobileccp.db.entity.Customer
import java.text.DecimalFormat
import java.text.NumberFormat


class ListCustomerAdapter(
    private var mList: List<Customer>,
    private val listener: CustomerSelectListener,
    private val isLookup: Boolean = false
) : RecyclerView.Adapter<ListCustomerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_listcustomer_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        val formatter: NumberFormat = DecimalFormat("#,###")

        holder.txtCustomer.text = ItemsViewModel.shipname
        holder.txtAddress.text = ItemsViewModel.shipaddress
        holder.txtPartnerName.text = ItemsViewModel.partnername

        val jenjang = ItemsViewModel.jenjang + ", NPSN : " + ItemsViewModel.npsn;
        holder.txtJenjang.text = jenjang

        val initial = ItemsViewModel.shipname.first().toString()
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(Color.parseColor("#A9A9A9")) // Fixed color from colors.xml
        holder.iconImageView.setImageDrawable(drawable)
        holder.iconTextView.text = initial

        holder.lnCustomer.setOnClickListener{
            listener.onSelect(ItemsViewModel, position)
        }
        holder.lnCustomerIcon.setOnClickListener{
            listener.onSelect(ItemsViewModel, position)
        }

        if (isLookup){
            holder.callButton.visibility = View.GONE
        }else {
            holder.callButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${ItemsViewModel.shiphp}")
                }
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCustomer: TextView = itemView.findViewById(R.id.txtCustomer)
        val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)
        val txtPartnerName: TextView = itemView.findViewById(R.id.txtPartnerName)
        val txtJenjang: TextView = itemView.findViewById(R.id.txtJenjang)
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val iconTextView: TextView = itemView.findViewById(R.id.iconTextView)
        val callButton: ImageButton = itemView.findViewById(R.id.callButton)

        val lnCustomer: LinearLayout = itemView.findViewById(R.id.lnCustomer)
        val lnCustomerIcon: FrameLayout = itemView.findViewById(R.id.lnCustomerIcon)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Customer>) {
        mList = newList
        notifyDataSetChanged()
    }
}


interface CustomerSelectListener {
    fun onSelect(cust: Customer, position: Int)
}
