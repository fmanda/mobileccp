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



class ListKelurahanAdapter(private var mList: List<String>, private var selected: Int, private var listener: SelectKelurahanListener) : RecyclerView.Adapter<ListKelurahanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_listkelurahan_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtKelurahan.text = mList[position]

        if (position == selected) {
//            holder.txtMerk.setBackgroundColor( R.drawable.bottom_background_2 )
            holder.txtKelurahan.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bottom_background)
        } else {
//            holder.txtMerk.setBackgroundColor( R.drawable.bottom_background_grey )
            holder.txtKelurahan.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bottom_background_grey)
        }

        holder.lnKelurahan.setOnClickListener(){
            listener.onSelected(position, mList[position])
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtKelurahan: TextView = itemView.findViewById(R.id.txtKelurahan)
        val lnKelurahan: LinearLayout = itemView.findViewById(R.id.lnKelurahan)
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