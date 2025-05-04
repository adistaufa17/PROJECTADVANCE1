package com.adista.projectadvance1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {
    val searchQuery = MutableLiveData<String>("")

    fun onFilterClick() {
        // Handle filter click
    }

    fun onFriendsClick() {
        // Handle friends click
    }

    fun onProfileClick() {
        // Handle profile click
    }

    fun onLogoutClick() {
        // Handle logout click
    }

    fun onSeeAllClick() {
        // Handle see all click
    }
}
