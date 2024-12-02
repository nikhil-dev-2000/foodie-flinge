package com.example.foodiefling.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiefling.R
import com.example.foodiefling.ui.User

class ChatUserAdapter(val context: Context, val userList: ArrayList<User>, val userId: String ):  // list of the matched users for recycler view
    RecyclerView.Adapter<ChatUserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        val name = currentUser.firstName + " " + currentUser.lastName
        holder.txtName.text = name
        holder.itemView.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.firstName+" "+currentUser.lastName)
            intent.putExtra("uid", "User_${currentUser.userId}")
            intent.putExtra("senderUid", userId)
            context.startActivity(intent)
        }
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val txtName = itemView.findViewById<TextView>(R.id.txt_Name)
    }
}