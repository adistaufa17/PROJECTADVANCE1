package com.adista.projectadvance1.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adista.projectadvance1.core.network.ApiService
import com.adista.projectadvance1.model.FriendData
import com.crocodic.core.data.CoreSession
import com.crocodic.core.helper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val session: CoreSession,
    private val apiService: ApiService
) : ViewModel() {

    val searchQuery = MutableLiveData<String>()

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _navigateToProfile = MutableLiveData<Boolean>()
    val navigateToProfile: LiveData<Boolean> get() = _navigateToProfile

    private val _friendList = MutableLiveData<List<FriendData>>()
    val friendList: LiveData<List<FriendData>> = _friendList

    private var originalList: List<FriendData> = emptyList()

    private val _navigateToFriends = MutableLiveData<Boolean>()
    val navigateToFriends: LiveData<Boolean> = _navigateToFriends

    private val _userPhoto = MutableLiveData<String?>()
    val userPhoto: LiveData<String?> = _userPhoto

    init {
        _userName.value = session.getString("USER_NAME") ?: "Your Name"
        _userPhoto.value = session.getString("USER_PHOTO")
    }

    fun onFriendsClick() {
        println("Friends clicked")
        // Tambahkan intent kalau sudah siap
    }

    fun onProfileClick() {
        _navigateToProfile.value = true
    }

    fun doneNavigating() {
        _navigateToProfile.value = false
    }

    fun doneNavigateToFriends() {
        _navigateToFriends.postValue(false)
    }


    fun getFriends(context: Context) = viewModelScope.launch {
        if (!NetworkHelper.isNetworkAvailable(context)) {
            println("Tidak ada koneksi internet.")
            return@launch
        }

        try {
            val response = apiService.getFriends()

            if (response.data.isNotEmpty()) {
                originalList = response.data
                _friendList.postValue(originalList)
                _userName.postValue(session.getString("USER_NAME"))
            } else {
                Log.w("FRIEND_API", "Data teman kosong dari server")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FRIEND_API", "Error saat mengambil data teman", e)
        }
    }


    fun updateFcmToken(token: String) = viewModelScope.launch {
        try {
            val response = apiService.updateFcmToken(token)

            if (response.isSuccessful) {
                Log.d("FCM", "Token berhasil dikirim")
            } else {
                Log.e("FCM", "Gagal kirim token: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("FCM", "Error: ${e.message}")
        }
    }

    fun refreshProfilePhoto() {
        _userPhoto.postValue(session.getString("USER_PHOTO"))
    }

    fun refreshAllData(context: Context) {
        getFriends(context) // ambil ulang list teman
        _userName.postValue(session.getString("USER_NAME")) // refresh nama dari session
        _userPhoto.postValue(session.getString("USER_PHOTO")) // refresh foto dari session
    }

    fun onLogoutClick() {
        session.clearAll()
        println("Logout clicked")
        // Tambahkan intent ke LoginActivity kalau mau auto logout
    }

    fun onSeeAllClick() {
        println("See All clicked")
        _friendList.postValue(originalList)
        searchQuery.value = ""
        _navigateToFriends.postValue(true)
    }


    fun onSearchQueryChanged(query: String) {
        val filtered = originalList.filter {
            it.name.contains(query, ignoreCase = true)
        }
        _friendList.postValue(filtered)
    }
}
