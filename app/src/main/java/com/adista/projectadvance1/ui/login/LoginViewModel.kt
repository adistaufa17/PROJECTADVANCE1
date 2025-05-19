package com.adista.projectadvance1.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    val phoneNumber = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _loginEvent = MutableLiveData<Unit>()
    val loginEvent: LiveData<Unit> = _loginEvent

    fun onLoginClick() {
        _loginEvent.value = Unit
    }
}
