package com.adista.projectadvance1.friends

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ActivityFriendDetailBinding
import com.adista.projectadvance1.model.FriendData
import com.google.gson.Gson
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendDetailBinding
    private val viewModel: FriendDetailViewModel by viewModels()
    private lateinit var friend: FriendData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend_detail)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val json = intent.getStringExtra("FRIEND_DATA")
        if (json != null) {
            friend = Gson().fromJson(json, FriendData::class.java)
            bindFriendData()
        } else {
            Toast.makeText(this, "Data teman tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnColek.setOnClickListener {
            viewModel.pokeFriend(friend.id, this)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnWhatsapp.setOnClickListener {
            viewModel.openWhatsapp(this, friend.phone)
        }

    }

    private fun bindFriendData() {
        viewModel.setFriendData(friend.name, friend.phone, friend.school)

        if (!friend.photo.isNullOrBlank()) {
            Glide.with(this)
                .load(friend.photo)
                .centerCrop()
                .into(binding.ivProfile)
            Log.d("PHOTO_URL", "Friend photo URL: ${friend.photo}")

        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_person)
        }
    }
}
