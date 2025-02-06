package com.example.promanager.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.promanager.R
import com.example.promanager.module.Card

class CardItemAdapter(val context: Context,var list:ArrayList<Card>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListener:OnClickListener? =null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
     return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]

        if(holder is MyViewHolder)
        {
            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text=model.name
        }
    }

    fun setOnclickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }

    interface OnClickListener{
       fun onClick(position: Int, card:Card)
    }

    private class MyViewHolder(view: View):RecyclerView.ViewHolder(view){}


}