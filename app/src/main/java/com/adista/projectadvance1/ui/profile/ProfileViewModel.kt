package com.adista.projectadvance1.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adista.projectadvance1.Resource
import com.adista.projectadvance1.SessionManager
import com.adista.projectadvance1.data.model.AuthResponse
import com.adista.projectadvance1.model.UserData
import com.adista.projectadvance1.core.di.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _profileData = MutableLiveData<Resource<UserData>>()
    val profileData: LiveData<Resource<UserData>> = _profileData

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    fun fetchProfile() {
        _loadingState.value = true

        apiService.getProfile().enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                _loadingState.value = false

                // Logging untuk debugging
                Log.d("ProfileViewModel", "Profile response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d("ProfileViewModel", "Profile response body: $body")

                    if (body.status == "success" && body.data != null) {
                        _profileData.value = Resource.Success(body.data)

                        // Simpan data ke session
                        sessionManager.saveUser(body.data)
                    } else {
                        _profileData.value = Resource.Error(body.message)
                    }
                } else {
                    // Error handling
                    val errorMsg = "Failed to get profile data: ${response.message()}"
                    Log.e("ProfileViewModel", errorMsg)
                    _profileData.value = Resource.Error(errorMsg)

                    try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            Log.e("ProfileViewModel", "Error body: $errorBody")
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Error reading error body", e)
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _loadingState.value = false
                Log.e("ProfileViewModel", "Profile fetch failure", t)
                _profileData.value = Resource.Error("Network error: ${t.message}")
            }
        })
    }

    fun getLocalProfile(): UserData? {
        return if (sessionManager.isLoggedIn()) {
            sessionManager.getUserData()
        } else {
            null
        }
    }
}
