package com.adista.projectadvance1.register

import androidx.lifecycle.*
import com.adista.projectadvance1.core.network.ApiService
import com.adista.projectadvance1.request.RegisterRequest
import com.crocodic.core.data.CoreSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession
) : ViewModel() {

    val name = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val school = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    fun register(name: String, phone: String, school: String, password: String, passwordConfirmation: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = RegisterRequest(name, phone, school, password, passwordConfirmation)
                val response = apiService.register(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    // Simpan ke session (gunakan data dari body jika perlu)
                    session.setValue("USER_NAME", name)
                    session.setValue("USER_PHONE", phone)
                    session.setValue("USER_SCHOOL", school)
                    session.setValue("IS_LOGGED_IN", true)

                    _registerSuccess.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Gagal register: ${errorBody ?: "Unknown error"}"
                    _registerSuccess.value = false
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
                _registerSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun onRegisterClick() {
        val nameValue = name.value.orEmpty()
        val phoneValue = phone.value.orEmpty()
        val schoolValue = school.value.orEmpty()
        val passwordValue = password.value.orEmpty()
        val confirmPasswordValue = confirmPassword.value.orEmpty()

        if (passwordValue != confirmPasswordValue) {
            _errorMessage.value = "Password tidak cocok"
            return
        }

        register(nameValue, phoneValue, schoolValue, passwordValue, confirmPasswordValue)
    }
}
