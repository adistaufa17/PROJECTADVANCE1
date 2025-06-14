package com.adista.projectadvance1.profil

import androidx.lifecycle.*
import com.adista.projectadvance1.core.network.ApiService
import com.crocodic.core.data.CoreSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
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

    val photoUrl = MutableLiveData<String?>()

    init {
        _name.value = session.getString("USER_NAME")
        _phone.value = session.getString("USER_PHONE")
        _school.value = session.getString("USER_SCHOOL")
        photoUrl.value = session.getString("USER_PHOTO")
    }


    fun updatePhoto(photoBase64: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val token = session.getString("user_token") ?: return@launch
                val body = mapOf(
                    "name" to (_name.value ?: ""),
                    "school" to (_school.value ?: ""),
                    "photo" to photoBase64
                )
                val response = apiService.editProfile("Bearer $token", body)

                if (response.isSuccessful) {
                    val result = response.body()

                    if (result?.code == 200) {
                        val photoUrlResult = result.data.photo

                        if (!photoUrlResult.isNullOrEmpty()) {
                            session.setValue("USER_PHOTO", photoUrlResult)
                            photoUrl.postValue(photoUrlResult)

                            // Debug log
                            println("Photo URL saved: $photoUrlResult")

                            onResult(true, result.message)
                        } else {
                            onResult(false, "Gagal mendapatkan URL foto")
                        }
                    } else {
                        onResult(false, result?.message ?: "Gagal mengupdate foto")
                    }
                } else {
                    onResult(false, "Gagal mengupdate foto: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Terjadi kesalahan: ${e.message}")
            }
        }
    }

    fun updateProfile(name: String, school: String) {
        viewModelScope.launch {
            try {
                val token = session.getString("user_token") ?: return@launch
                val body = mapOf(
                    "name" to name,
                    "school" to school,
                    "photo" to (photoUrl.value ?: "")
                )
                val response = apiService.editProfile("Bearer $token", body)

                if (response.isSuccessful && response.body()?.code == 200) {
                    session.setValue("USER_NAME", name)
                    session.setValue("USER_SCHOOL", school)
                    _name.postValue(name)
                    _school.postValue(school)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun changePassword(current: String, newPass: String, confirm: String, onResult: (Boolean, String) -> Unit) {
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
                    val errorBody = response.errorBody()?.string()
                    val message = JSONObject(errorBody ?: "{}").optString("message", "Gagal mengubah password")
                    onResult(false, message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Terjadi kesalahan: ${e.message}")
            }
        }
    }

    fun onLogoutClick() {
        session.clearAll()
    }

    fun onBackClick() {
        // opsional jika ingin kembali manual
    }


}
