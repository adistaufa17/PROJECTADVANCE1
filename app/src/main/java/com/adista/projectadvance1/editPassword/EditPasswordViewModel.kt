package com.adista.projectadvance1.editPassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adista.projectadvance1.core.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class EditPasswordViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    val currentPassword = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    fun changePassword(onResult: (Boolean, String) -> Unit) {
        val current = currentPassword.value ?: ""
        val newPass = newPassword.value ?: ""
        val confirm = confirmPassword.value ?: ""

        if (newPass.length < 6) {
            onResult(false, "Password minimal 6 karakter")
            return
        }

        if (newPass != confirm) {
            onResult(false, "Konfirmasi password tidak cocok")
            return
        }

        viewModelScope.launch {
            try {
                val body = mapOf(
                    "current_password" to current,
                    "new_password" to newPass,
                    "new_password_confirmation" to confirm
                )
                val response = apiService.changePassword(body)
                if (response.isSuccessful) {
                    onResult(true, "Password berhasil diubah")
                } else {
                    val errorMsg = response.errorBody()?.string()
                    val message = JSONObject(errorMsg ?: "{}").optString("message", "Gagal mengubah password")
                    onResult(false, message)
                }
            } catch (e: Exception) {
                onResult(false, "Terjadi kesalahan: ${e.message}")
            }
        }
    }
}
