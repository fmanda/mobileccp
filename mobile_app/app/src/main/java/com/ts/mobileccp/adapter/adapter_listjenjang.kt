package com.ts.mobileccp.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ts.mobileccp.R



class ListJenjangAdapter(private var mList: List<String>, private var selected: Int, private var listener: SelectKelurahanListener) : RecyclerView.Adapter<ListJenjangAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_listjenjang_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtJenjang.text = mList[position]

        if (position == selected) {
//            holder.txtMerk.setBackgroundColor( R.drawable.bottom_background_2 )
            holder.txtJenjang.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bottom_background)
        } else {
//            holder.txtMerk.setBackgroundColor( R.drawable.bottom_background_grey )
            holder.txtJenjang.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bottom_background_grey)
        }

        holder.lnJenjang.setOnClickListener(){
            listener.onSelected(position, mList[position])
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtJenjang: TextView = itemView.findViewById(R.id.txtJenjang)
        val lnJenjang: LinearLayout = itemView.findViewById(R.id.lnJenjang)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectKelurahan(idx : Int){
        selected = idx
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<String>) {
        mList = newList
        notifyDataSetChanged()
    }


}

interface SelectKelurahanListener {
    fun onSelected(position: Int, kelurahan: String)
}