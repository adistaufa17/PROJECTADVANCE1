package com.adista.projectadvance1.friends

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adista.projectadvance1.util.ImageUtil
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ActivityFriendDetailBinding
import com.adista.projectadvance1.model.FriendData
import com.bumptech.glide.Glide
import com.google.gson.Gson
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

        // Ambil data dari intent
        val json = intent.getStringExtra("FRIEND_DATA")
        if (json.isNullOrBlank()) {
            Toast.makeText(this, "Data teman tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        friend = Gson().fromJson(json, FriendData::class.java)

        // Isi data ke ViewModel dan tampilkan di UI
        bindFriendData()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnColek.setOnClickListener {
            viewModel.pokeFriend(friend.id, this)
        }

        binding.btnWhatsapp.setOnClickListener {
            Log.d("WHATSAPP", "Tombol WhatsApp diklik: ${friend.phone}")
            viewModel.openWhatsapp(this, friend.phone)
        }
    }

    private fun bindFriendData() {
        viewModel.setFriendData(friend.name, friend.phone, friend.school)

        val imageUrl = ImageUtil.getFullImageUrl(friend.photo)

        if (imageUrl != null) {
            if (imageUrl.isNotBlank()) {
                Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(binding.ivProfile)
                Log.d("PHOTO_URL", "Friend photo URL: $imageUrl")
            } else {
                binding.ivProfile.setImageResource(R.drawable.ic_person)
            }
        }
    }
}
