package com.adista.projectadvance1

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adista.projectadvance1.core.network.ApiService
import com.adista.projectadvance1.model.NotificationModel
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.base.viewmodel.CoreViewModel
import com.crocodic.core.data.CoreSession
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val session: CoreSession,
    private val apiService: ApiService
) : ViewModel() {

    val notifications = MutableLiveData<List<NotificationModel>>()

    fun getNotifications() = viewModelScope.launch {
        try {
            val token = session.getString("USER_TOKEN") ?: return@launch
            val bearer = "Bearer $token"
            val response = apiService.getNotifications(bearer)

            if (response.isSuccessful) {
                val dataArray = response.body()?.data as? List<*>
                val notifList = dataArray?.mapNotNull {
                    Gson().fromJson(Gson().toJson(it), NotificationModel::class.java)
                } ?: emptyList()

                notifications.postValue(notifList)
            } else {
                Log.e("NOTIF", "Gagal: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("NOTIF", "Exception: ${e.message}")
        }
    }

}
