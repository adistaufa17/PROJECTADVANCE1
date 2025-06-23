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

    fun onLogoutClick() {
        session.clearAll()
    }

    fun loadProfileData() = viewModelScope.launch {
        try {
            val response = apiService.getProfile()

            if (response.isSuccessful) {
                val data = response.body()?.data
                data?.let {
                    _name.postValue(it.name)
                    _school.postValue(it.school ?: "")
                    photoUrl.postValue(it.photo)

                    session.setValue("USER_NAME", it.name)
                    session.setValue("USER_SCHOOL", it.school ?: "")
                    session.setValue("USER_PHOTO", it.photo ?: "")
                }
            } else {
            }
        } catch (e: Exception) {
        }
    }


}
