package com.adista.projectadvance1.friends

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ItemFriendBinding
import com.adista.projectadvance1.model.FriendData
import com.bumptech.glide.Glide
import timber.log.Timber

class FriendAdapter(
    private var friends: List<FriendData>,
    private val onClick: (FriendData) -> Unit
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

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
            tvSchool.text = friend.school ?: "-"
            root.setOnClickListener {
                onClick(friend)
            }

            if (!friend.photo.isNullOrBlank()) {
                Glide.with(holder.itemView.context)
                    .load(friend.photo)
                    .centerCrop()
                    .into(ivProfile)
            } else {
                ivProfile.setImageResource(R.drawable.ic_person)
            }
        }
    }

    override fun getItemCount(): Int = friends.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<FriendData>) {
        Timber.tag("FriendAdapter").d("%s items", "Updating list with " + newList.size)
        friends = newList
        notifyDataSetChanged()
    }
}
