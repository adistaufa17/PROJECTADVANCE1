package com.adista.projectadvance1.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adista.projectadvance1.databinding.ItemFriendBinding
import com.adista.projectadvance1.model.FriendData

class FriendAdapter(private var friends: List<FriendData>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        with(holder.binding) {
            tvName.text = friend.name
            tvSchool.text = friend.school
        }
    }

    override fun getItemCount(): Int = friends.size

    fun updateData(newList: List<FriendData>) {
        friends = newList
        notifyDataSetChanged()
    }
}
