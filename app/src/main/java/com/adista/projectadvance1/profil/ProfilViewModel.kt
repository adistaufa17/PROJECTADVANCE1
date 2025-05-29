package com.adista.projectadvance1.profil


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adista.projectadvance1.core.network.ApiService
import com.crocodic.core.data.CoreSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfilViewModel @Inject constructor(
    private val session: CoreSession,
    private val apiService: ApiService
) : ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> = _phone

    private val _school = MutableLiveData<String>()
    val school: LiveData<String> = _school

    init {
        _name.value = session.getString("USER_NAME")
        _phone.value = session.getString("USER_PHONE")
        _school.value = session.getString("USER_SCHOOL")
    }

    fun onBackClick() {
        // tambahkan logika kembali jika perlu
    }

    fun onEditClick() {
        // tambahkan logika edit jika perlu
    }

    fun onChangePasswordClick() {
        // tambahkan logika ubah password jika perlu
    }

    fun onLogoutClick() {
        session.clearAll()
    }
}
