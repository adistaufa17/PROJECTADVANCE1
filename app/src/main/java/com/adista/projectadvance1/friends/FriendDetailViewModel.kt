package com.adista.projectadvance1.friends

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adista.projectadvance1.core.network.ApiService
import com.crocodic.core.data.CoreSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendDetailViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession
) : ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> = _phone

    private val _school = MutableLiveData<String>()
    val school: LiveData<String> = _school

    fun setFriendData(name: String, phone: String, school: String?) {
        _name.value = name
        _phone.value = phone
        _school.value = school ?: "-"
    }

    fun onBackClick() {
    }

    fun onWhatsappClick() {
    }

    fun openWhatsapp(context: Context, rawPhone: String?) {
        val phone = rawPhone
            ?.replace(" ", "")
            ?.replace("-", "")
            ?.replace("+", "")
            ?.replace("^0", "62")
            ?: return Toast.makeText(context, "Nomor tidak tersedia", Toast.LENGTH_SHORT).show()

        try {
            val uri = Uri.parse("https://wa.me/$phone")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.whatsapp")
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak dapat membuka WhatsApp. Pastikan nomor aktif atau WhatsApp terpasang.", Toast.LENGTH_SHORT).show()
        }
    }


    fun pokeFriend(friendId: Int, context: Context) = viewModelScope.launch {
        try {
            val token = session.getString("USER_TOKEN") ?: return@launch
            val bearer = "Bearer $token"
            val response = apiService.pokeFriend(bearer, friendId)
            if (response.isSuccessful) {
                Toast.makeText(context, "Teman berhasil dicolek!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal mencolek teman", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
