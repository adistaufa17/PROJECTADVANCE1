package com.adista.projectadvance1.editProfil

import androidx.lifecycle.*
import com.adista.projectadvance1.core.network.ApiService
import com.crocodic.core.data.CoreSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val session: CoreSession,
    private val apiService: ApiService
) : ViewModel() {

    val name = MutableLiveData<String>()
    val school = MutableLiveData<String?>()
    val photoUrl = MutableLiveData<String?>()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        name.value = session.getString("USER_NAME")
        school.value = session.getString("USER_SCHOOL")
        photoUrl.value = session.getString("USER_PHOTO")
    }

    fun updateProfile(photoBase64: String? = null, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val token = session.getString("user_token") ?: return@launch
                val body = mutableMapOf<String, String>(
                    "name" to (name.value ?: ""),
                    "school" to (school.value ?: "")
                )
                if (!photoBase64.isNullOrEmpty()) {
                    body["photo"] = photoBase64
                }

                val response = apiService.editProfile(body)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.code == 200) {
                        val data = result.data

                        // Update session
                        session.setValue("USER_NAME", data.name)
                        data.school?.let { session.setValue("USER_SCHOOL", it) }
                        data.photo?.let { session.setValue("USER_PHOTO", it) }

                        // Update LiveData
                        name.postValue(data.name)
                        school.postValue(data.school)
                        photoUrl.postValue(data.photo)

                        onResult(true, "Profil berhasil diperbarui")
                    } else {
                        onResult(false, result?.message ?: "Gagal update profil")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val msg = JSONObject(errorBody ?: "{}").optString("message", "Gagal update profil")
                    onResult(false, msg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Terjadi kesalahan: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun onLogoutClick() {
        session.clearAll()
    }
}
