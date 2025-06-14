package com.adista.projectadvance1.friends

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adista.projectadvance1.MainViewModel
import com.adista.projectadvance1.databinding.ActivityFriendsBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: FriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = FriendAdapter(emptyList()) { selectedFriend ->
            val intent = Intent(this, FriendDetailActivity::class.java)
            intent.putExtra("FRIEND_DATA", Gson().toJson(selectedFriend))
            startActivity(intent)
        }

        binding.rvFriends.layoutManager = LinearLayoutManager(this)
        binding.rvFriends.adapter = adapter

        viewModel.friendList.observe(this) {
            adapter.updateData(it)
        }

        viewModel.getFriends(this)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
