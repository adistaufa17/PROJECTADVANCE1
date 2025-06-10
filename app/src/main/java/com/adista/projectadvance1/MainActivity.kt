package com.adista.projectadvance1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.adista.projectadvance1.databinding.ActivityMainBinding
import com.adista.projectadvance1.friends.FriendAdapter
import com.adista.projectadvance1.friends.FriendDetailActivity
import com.adista.projectadvance1.login.LoginActivity
import com.adista.projectadvance1.profil.ProfileActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.crocodic.core.data.CoreSession
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
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
            sendTokenToServer(token) // ✅ benar
        }

        // ✅ Satu-satunya adapter yang digunakan
        friendAdapter = FriendAdapter(emptyList()) { selectedFriend ->
            val intent = Intent(this, FriendDetailActivity::class.java)
            intent.putExtra("FRIEND_DATA", Gson().toJson(selectedFriend))
            startActivity(intent)
        }

        binding.rvFriends.layoutManager = LinearLayoutManager(this)
        binding.rvFriends.adapter = friendAdapter

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.ivUserAvatar.setOnClickListener { goToProfile() }
        binding.tvUserName.setOnClickListener { goToProfile() }

        binding.btnLogout.setOnClickListener {
            session.clearAll()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
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

        viewModel.friendList.observe(this) { list ->
            println("Received friend list: ${list.size}")
            friendAdapter.updateData(list)
        }

        viewModel.getFriends(this)


    }

    override fun onResume() {
        super.onResume()
        val token = session.getString("USER_TOKEN")
        if (!token.isNullOrEmpty()) {
            viewModel.getFriends(this)
        } else {
            Log.e("MAIN", "Token kosong, tidak bisa ambil teman")
        }
    }

    private fun goToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun sendTokenToServer(token: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.106.2:8000/api/update-fcm-token"

        val request = object : StringRequest(Method.POST, url,
            { response ->
                Log.d("FCM", "Token berhasil dikirim ke server")
            },
            { error ->
                Log.e("FCM", "Gagal kirim token: ${error.message}")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = mutableMapOf<String, String>()
                headers["Authorization"] = "Bearer ${session.getString("USER_TOKEN")}"
                return headers
            }

            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf("fcm_token" to token)
            }
        }

        queue.add(request)
    }


    private fun getUserIdFromPrefs(): Int {
        return session.getInt("USER_ID") ?: 0
    }


}
