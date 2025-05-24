package com.adista.projectadvance1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    // Untuk search input di EditText
    val searchQuery = MutableLiveData<String>()

    // Event navigasi ke daftar teman
    private val _navigateToFriends = MutableLiveData<Unit>()
    val navigateToFriends: LiveData<Unit> get() = _navigateToFriends

    // Event navigasi ke profil
    private val _navigateToProfile = MutableLiveData<Unit>()
    val navigateToProfile: LiveData<Unit> get() = _navigateToProfile

    // Event logout
    private val _logout = MutableLiveData<Unit>()
    val logout: LiveData<Unit> get() = _logout

    // Event "See All"
    private val _seeAllFriends = MutableLiveData<Unit>()
    val seeAllFriends: LiveData<Unit> get() = _seeAllFriends

    // Dipanggil saat tombol "Friends" diklik
    fun onFriendsClick() {
        _navigateToFriends.value = Unit
    }

    // Dipanggil saat tombol "Profile" diklik
    fun onProfileClick() {
        _navigateToProfile.value = Unit
    }

    // Dipanggil saat tombol "Logout" diklik
    fun onLogoutClick() {
        _logout.value = Unit
    }

    // Dipanggil saat tombol "See All" diklik
    fun onSeeAllClick() {
        _seeAllFriends.value = Unit
    }
}
