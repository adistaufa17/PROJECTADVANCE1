package com.adista.projectadvance1

import android.content.Context
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

    init {
        _userName.value = session.getString("USER_NAME") ?: "Your Name"
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

    fun getFriends(context: Context) = viewModelScope.launch {
        if (!NetworkHelper.isNetworkAvailable(context)) {
            println("Tidak ada koneksi internet.")
            return@launch
        }

        try {
            val token = session.getString("TOKEN") ?: return@launch
            val bearerToken = "Bearer $token"
            val response = apiService.getFriends(bearerToken)
            _friendList.postValue(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun onLogoutClick() {
        session.clearAll()
        println("Logout clicked")
        // Tambahkan intent ke LoginActivity kalau mau auto logout
    }

    fun onSeeAllClick() {
        println("See All clicked")
        // Tambahkan logic navigasi jika sudah ada halaman FriendList
    }
}
