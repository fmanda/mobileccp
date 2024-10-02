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



class ListMerkAdapter(private var mList: List<String>, private var selectedMerk: Int, private var listener: SelectMerkListener) : RecyclerView.Adapter<ListMerkAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_listmerk_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtMerk.text = mList[position]

        if (position == selectedMerk) {
//            holder.txtMerk.setBackgroundColor( R.drawable.bottom_background_2 )
            holder.txtMerk.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bottom_background)
        } else {
//            holder.txtMerk.setBackgroundColor( R.drawable.bottom_background_grey )
            holder.txtMerk.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bottom_background_grey)
        }

        holder.lnMerk.setOnClickListener(){
            listener.onSelectedMerk(position, mList[position])
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtMerk: TextView = itemView.findViewById(R.id.txtMerk)
        val lnMerk: LinearLayout = itemView.findViewById(R.id.lnMerk)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedMerk(idx : Int){
        selectedMerk = idx
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<String>) {
        mList = newList
        notifyDataSetChanged()
    }


}

interface SelectMerkListener {
    fun onSelectedMerk(position: Int, merk: String)
}