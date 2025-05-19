package com.adista.projectadvance1.ui.editProfile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adista.projectadvance1.Resource
import com.adista.projectadvance1.SessionManager
import com.adista.projectadvance1.data.request.EditProfileRequest
import com.adista.projectadvance1.data.model.AuthResponse
import com.adista.projectadvance1.model.UserData
import com.adista.projectadvance1.core.di.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _profileUpdateResult = MutableLiveData<Resource<UserData>>()
    val profileUpdateResult: LiveData<Resource<UserData>> = _profileUpdateResult

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> = _userData

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    fun getCurrentUserData() {
        val userData = sessionManager.getUserData()
        _userData.value = userData
    }

    fun updateProfile(name: String, school: String, imageUri: Uri?, context: Context) {
        _loadingState.value = true

        try {
            // Convert image to Base64 if selected
            val base64Image = imageUri?.let { convertImageToBase64(it, context) }

            val request = EditProfileRequest(
                name = name,
                school = school,
                photo = base64Image
            )

            apiService.updateProfile(request).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    _loadingState.value = false

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        Log.d("EditProfileViewModel", "Update response: $body")

                        if (body.status == "success" && body.data != null) {
                            // Update session data
                            sessionManager.saveUser(body.data)
                            _profileUpdateResult.value = Resource.Success(body.data)
                        } else {
                            _profileUpdateResult.value = Resource.Error(body.message ?: "Failed to update profile")
                        }
                    } else {
                        _profileUpdateResult.value = Resource.Error("Failed to update profile: ${response.message()}")

                        try {
                            val errorBody = response.errorBody()?.string()
                            Log.e("EditProfileViewModel", "Error body: $errorBody")
                        } catch (e: Exception) {
                            Log.e("EditProfileViewModel", "Error reading error body", e)
                        }
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    _loadingState.value = false
                    Log.e("EditProfileViewModel", "Update profile error", t)
                    _profileUpdateResult.value = Resource.Error("Network error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            _loadingState.value = false
            Log.e("EditProfileViewModel", "Exception in updateProfile", e)
            _profileUpdateResult.value = Resource.Error("Error: ${e.message}")
        }
    }

    private fun convertImageToBase64(uri: Uri, context: Context): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                // Kompres gambar untuk mengurangi ukuran
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                val compressedBitmap = compressBitmap(bitmap)

                val outputStream = ByteArrayOutputStream()
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                val byteArray = outputStream.toByteArray()

                val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
                "data:image/jpeg;base64,$base64Image" // Format yang biasa diterima server
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("EditProfileViewModel", "Error converting image to base64", e)
            null
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        // Target maksimum ukuran gambar (contoh: 800x800 piksel)
        val maxWidth = 800
        val maxHeight = 800

        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap // Tidak perlu kompresi jika ukuran sudah kecil
        }

        val ratio = width.toFloat() / height.toFloat()

        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxWidth
            newHeight = (maxWidth / ratio).toInt()
        } else {
            newHeight = maxHeight
            newWidth = (maxHeight * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
