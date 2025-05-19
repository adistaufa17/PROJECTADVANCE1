package com.adista.projectadvance1.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {
    val name = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val school = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    private val _registerEvent = MutableLiveData<Unit>()
    val registerEvent: LiveData<Unit> = _registerEvent

    fun onRegisterClick() {
        _registerEvent.value = Unit
    }
}
