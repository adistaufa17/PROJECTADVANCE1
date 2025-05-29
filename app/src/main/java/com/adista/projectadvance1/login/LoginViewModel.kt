package com.adista.projectadvance1.login

import androidx.lifecycle.*
import com.adista.projectadvance1.core.network.ApiService
import com.adista.projectadvance1.request.LoginRequest
import com.crocodic.core.data.CoreSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession
) : ViewModel() {

    val phone = MutableLiveData<String>()
    val password = MutableLiveData<String>()

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

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.code == 200 && body.status == "success" && body.data != null) {
                        session.setValue("USER_NAME", body.data.name)
                        session.setValue("USER_PHONE", body.data.phone)
                        session.setValue("USER_SCHOOL", body.data.school ?: "")
                        session.setValue("IS_LOGGED_IN", true)
                        session.setValue("USER_TOKEN", body.data.token)

                        _loginResult.value = true
                    } else {
                        _errorMessage.value = body?.message ?: "Login gagal"
                        _loginResult.value = false
                    }
                } else {
                    _errorMessage.value = "Login gagal: ${response.message()}"
                    _loginResult.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login gagal, terjadi kesalahan"
                _loginResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onLoginClick() {
        val currentPhone = phone.value.orEmpty()
        val currentPassword = password.value.orEmpty()

        if (currentPhone.isBlank() || currentPhone.length < 10 || !currentPhone.all { it.isDigit() }) {
            _errorMessage.value = "Masukkan nomor HP yang valid"
            return
        }

        if (currentPassword.isBlank()) {
            _errorMessage.value = "Password tidak boleh kosong"
            return
        }

        login(currentPhone, currentPassword)
    }
}
