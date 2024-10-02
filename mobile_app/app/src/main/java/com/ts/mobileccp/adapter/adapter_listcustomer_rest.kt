package com.ts.mobileccp.adapter


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
import com.ts.mobileccp.rest.CustomerResponse
import java.text.DecimalFormat
import java.text.NumberFormat


class ListCustomerRestAdapter(
    private var mList: List<CustomerResponse>
) : RecyclerView.Adapter<ListCustomerRestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_listcustomer_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        val formatter: NumberFormat = DecimalFormat("#,###")

        holder.txtCustomer.text = ItemsViewModel.nama
        holder.txtAddress.text = ItemsViewModel.alamat

        val kelKecamatan = ItemsViewModel.kelurahan + ", " + ItemsViewModel.kecamatan;
        holder.txtKelurahan.text = kelKecamatan

        val initial = ItemsViewModel.nama.first().toString()
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(Color.parseColor("#A9A9A9")) // Fixed color from colors.xml
        holder.iconImageView.setImageDrawable(drawable)
        holder.iconTextView.text = initial


        holder.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${ItemsViewModel.phone}")
            }
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCustomer: TextView = itemView.findViewById(R.id.txtCustomer)
        val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)
        val txtKelurahan: TextView = itemView.findViewById(R.id.txtKelurahan)
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val iconTextView: TextView = itemView.findViewById(R.id.iconTextView)
        val callButton: ImageButton = itemView.findViewById(R.id.callButton)

        val lnCustomer: LinearLayout = itemView.findViewById(R.id.lnCustomer)
        val lnCustomerIcon: FrameLayout = itemView.findViewById(R.id.lnCustomerIcon)
    }

    fun updateData(newList: List<CustomerResponse>) {
        mList = newList
        notifyDataSetChanged()
    }
}

