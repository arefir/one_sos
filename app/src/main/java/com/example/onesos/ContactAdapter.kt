package com.example.onesos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onesos.databinding.ContactBinding

class ContactAdapter(val items:ArrayList<Contact>): RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(data: Contact)
    }

    var itemClickListener: OnItemClickListener?=null

    inner class ViewHolder(val binding: ContactBinding) : RecyclerView.ViewHolder(binding.root){
        init{
            binding.outerlayout.setOnClickListener {
                itemClickListener?.OnItemClick(items[adapterPosition])
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.contactName.text = items[position].name
        holder.binding.contactNo.text = items[position].number
    }

    override fun getItemCount(): Int {
        return items.size
    }

//    fun notifyAdd(position: Int) {
//        notifyItemInserted(holder.get)
//    }

}