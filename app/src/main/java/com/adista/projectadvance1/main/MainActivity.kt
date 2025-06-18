package com.adista.projectadvance1.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.adista.projectadvance1.R
import com.adista.projectadvance1.databinding.ActivityMainBinding
import com.adista.projectadvance1.friends.FriendAdapter
import com.adista.projectadvance1.friends.FriendDetailActivity
import com.adista.projectadvance1.friends.FriendsActivity
import com.adista.projectadvance1.login.LoginActivity
import com.adista.projectadvance1.profil.ProfileActivity
import com.adista.projectadvance1.util.ImageUtil
import com.bumptech.glide.Glide
import com.crocodic.core.data.CoreSession
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var session: CoreSession
    private lateinit var binding: ActivityMainBinding
    private lateinit var friendAdapter: FriendAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val userId = getUserIdFromPrefs() // Ambil ID user dari SharedPreferences atau session login

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            viewModel.updateFcmToken(token)
        }

        // Cek permission notifikasi untuk Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        friendAdapter = FriendAdapter(emptyList()) { selectedFriend ->
            val intent = Intent(this, FriendDetailActivity::class.java)
            intent.putExtra("FRIEND_DATA", Gson().toJson(selectedFriend))
            startActivity(intent)
        }

        binding.tvSeeAll.setOnClickListener {
            startActivity(Intent(this, FriendsActivity::class.java))
        }

        binding.categoryFriends.setOnClickListener {
            startActivity(Intent(this, FriendsActivity::class.java))
        }

        binding.rvFriends.layoutManager = LinearLayoutManager(this)
        binding.rvFriends.adapter = friendAdapter

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.ivUserAvatar.setOnClickListener { goToProfile() }
        binding.tvUserName.setOnClickListener { goToProfile() }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah kamu yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    session.clearAll()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                .setNegativeButton("Batal", null)
                .show()
        }


        viewModel.userName.observe(this) { name ->
            binding.tvUserName.text = name
        }

        viewModel.navigateToProfile.observe(this) { shouldNavigate ->
            if (shouldNavigate == true) {
                startActivity(Intent(this, ProfileActivity::class.java))
                viewModel.doneNavigating()
            }
        }

        viewModel.navigateToFriends.observe(this) { shouldNavigate ->
            if (shouldNavigate == true) {
                startActivity(Intent(this, FriendsActivity::class.java))
                viewModel.doneNavigateToFriends()
            }
        }


        viewModel.friendList.observe(this) { list ->
            println("Received friend list: ${list.size}")
            friendAdapter.updateData(list)
        }

        viewModel.getFriends(this)

        viewModel.searchQuery.observe(this) { query ->
            viewModel.onSearchQueryChanged(query)
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim()
                viewModel.onSearchQueryChanged(query)
                true
            } else false
        }

        viewModel.userPhoto.observe(this) { photoPath ->
            val imageUrl = ImageUtil.getFullImageUrl(photoPath)
            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(binding.ivUserAvatar)
        }

    }

    override fun onResume() {
        super.onResume()
        val token = session.getString("USER_TOKEN")
        if (!token.isNullOrEmpty()) {
            viewModel.getFriends(this)
            viewModel.refreshProfilePhoto()
        } else {
            Log.e("MAIN", "Token kosong, tidak bisa ambil teman")
        }
    }

    private fun goToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun getUserIdFromPrefs(): Int {
        return session.getInt("USER_ID") ?: 0
    }


}
