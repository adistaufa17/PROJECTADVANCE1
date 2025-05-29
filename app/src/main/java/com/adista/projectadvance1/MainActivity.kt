package com.adista.projectadvance1

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.adista.projectadvance1.databinding.ActivityMainBinding
import com.adista.projectadvance1.friends.FriendAdapter
import com.adista.projectadvance1.login.LoginActivity
import com.adista.projectadvance1.profil.ProfileActivity
import com.crocodic.core.data.CoreSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var session: CoreSession
    private lateinit var binding: ActivityMainBinding
    private lateinit var friendAdapter: FriendAdapter
    private val viewModel: MainViewModel by viewModels() // GUNAKAN INI SAJA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getFriends(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val adapter = FriendAdapter(emptyList())
        binding.rvFriends.layoutManager = LinearLayoutManager(this)
        binding.rvFriends.adapter = adapter

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.ivUserAvatar.setOnClickListener {
            goToProfile()
        }

        binding.tvUserName.setOnClickListener {
            goToProfile()
        }

        viewModel.userName.observe(this) { name ->
            binding.tvUserName.text = name
        }

        binding.btnLogout.setOnClickListener {
            session.clearAll()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity() // untuk clear semua activity
        }

        viewModel.navigateToProfile.observe(this) { shouldNavigate ->
            if (shouldNavigate == true) {
                startActivity(Intent(this, ProfileActivity::class.java))
                viewModel.doneNavigating()
            }
        }

        viewModel.friendList.observe(this) { list ->
            friendAdapter.updateData(list)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getFriends(this)
    }

    private fun goToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}
