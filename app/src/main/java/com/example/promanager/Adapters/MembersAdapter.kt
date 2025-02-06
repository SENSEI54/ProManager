package com.example.promanager.Adapters

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.promanager.R
import com.example.promanager.module.User

class MembersAdapter (private var context:Context, private var List:ArrayList<User>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MembersAdapter.MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_member, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = List[position]
        if(holder is MembersAdapter.MyViewHolder)
        {
            Glide
                .with(context)
                .load(model.image)
                .circleCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_member_image))

            holder.itemView.findViewById<TextView>(R.id.tv_member_name).text=model.name
            holder.itemView.findViewById<TextView>(R.id.tv_member_email).text= model.email

        }
    }

    private class MyViewHolder(view: View):RecyclerView.ViewHolder(view){

    }

}