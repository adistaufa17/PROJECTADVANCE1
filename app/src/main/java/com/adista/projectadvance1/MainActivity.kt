package com.adista.projectadvance1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.appcompat.app.AppCompatActivity
import com.adista.projectadvance1.databinding.ActivityMainBinding
import com.adista.projectadvance1.friends.FriendsActivity
import com.adista.projectadvance1.login.LoginActivity
import com.adista.projectadvance1.profil.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)

        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Lanjutkan tampilkan UI utama
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set ViewModel ke layout (data binding)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Observe navigasi atau aksi
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.navigateToFriends.observe(this, Observer {
            // TODO: Ganti dengan startActivity jika sudah ada activity tujuan
            startActivity(Intent(this, FriendsActivity::class.java))
        })

        viewModel.navigateToProfile.observe(this, Observer {
            // TODO: Ganti dengan startActivity jika sudah ada activity tujuan
            startActivity(Intent(this, ProfileActivity::class.java))
        })

        viewModel.logout.observe(this, Observer {
            // TODO: Logout logic, lalu kembali ke LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        })

        viewModel.seeAllFriends.observe(this, Observer {
            // TODO: Tambahkan aksi lihat semua teman
        })
    }

}