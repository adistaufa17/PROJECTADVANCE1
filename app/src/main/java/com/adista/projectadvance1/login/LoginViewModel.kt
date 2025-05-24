package com.adista.projectadvance1.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adista.projectadvance1.core.network.ApiService
import com.adista.projectadvance1.request.LoginRequest
import com.adista.projekadvance1.response.AuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    val phone = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _userToken = MutableLiveData<String>()
    val userToken: LiveData<String> = _userToken

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = LoginRequest(phone, password)
                val response = apiService.login(request)
                if (response.code == 200 && response.status == "success" && response.data != null) {
                    _userToken.value = response.data.token
                    _loginResult.value = true
                } else {
                    _errorMessage.value = response.message
                    _loginResult.value = false
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed"
                _loginResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onLoginClick() {
        val currentPhone = phone.value.orEmpty()
        val currentPassword = password.value.orEmpty()
        login(currentPhone, currentPassword)
    }
}
