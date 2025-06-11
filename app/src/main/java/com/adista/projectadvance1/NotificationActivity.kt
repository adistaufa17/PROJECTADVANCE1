package com.adista.projectadvance1

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adista.projectadvance1.databinding.ActivityNotificationBinding
import com.adista.projectadvance1.model.NotificationModel
import com.android.volley.toolbox.JsonObjectRequest

import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import dagger.hilt.android.AndroidEntryPoint
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var session: CoreSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        session = CoreSession(this)
        recyclerView = findViewById(R.id.rvNotification)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getNotifications()
    }

    private fun getNotifications() {
        val token = session.getString("USER_TOKEN") ?: return

        val request = object : JsonObjectRequest(
            Request.Method.GET,
            "http://192.168.106.2:8000/api/notifications",
            null,
            { response ->
                val jsonArray = response.getJSONArray("data")
                val notifList = mutableListOf<NotificationModel>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val model = Gson().fromJson(obj.toString(), NotificationModel::class.java)
                    notifList.add(model)
                }
                showNotificationList(notifList)
            },
            { error ->
                Toast.makeText(this, "Gagal ambil notifikasi", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Authorization" to "Bearer $token")
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun showNotificationList(list: List<NotificationModel>) {
        recyclerView.adapter = object : RecyclerView.Adapter<NotificationViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_2, parent, false)
                return NotificationViewHolder(view)
            }

            override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
                val notif = list[position]
                holder.title.text = notif.title
                holder.body.text = notif.body ?: ""
            }

            override fun getItemCount(): Int = list.size
        }
    }

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(android.R.id.text1)
        val body: TextView = view.findViewById(android.R.id.text2)
    }
}
