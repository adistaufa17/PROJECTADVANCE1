package com.adista.projectadvance1

import android.content.Context
import android.content.SharedPreferences
import com.adista.projectadvance1.model.UserData

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("KelasTeman", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        const val TOKEN = "token"
        const val IS_LOGGED_IN = "is_logged_in"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_PHONE = "user_phone"
        const val USER_SCHOOL = "user_school"
        const val USER_PROFILE = "user_profile"
    }

    fun saveAuthToken(token: String?) {
        if (token != null) {
            editor.putString(TOKEN, "Bearer $token")
            editor.apply()
        }
    }

    fun fetchAuthToken(): String? {
        return sharedPreferences.getString(TOKEN, null)
    }

    fun saveUser(user: UserData) {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putInt(USER_ID, user.id)
        editor.putString(USER_NAME, user.name)
        editor.putString(USER_PHONE, user.phoneNumber)
        editor.putString(USER_SCHOOL, user.school)
        editor.putString(USER_PROFILE, user.profileImage)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun getUserData(): UserData {
        return UserData(
            id = sharedPreferences.getInt(USER_ID, 0),
            name = sharedPreferences.getString(USER_NAME, "") ?: "",
            phoneNumber = sharedPreferences.getString(USER_PHONE, "") ?: "",
            school = sharedPreferences.getString(USER_SCHOOL, "") ?: "",
            profileImage = sharedPreferences.getString(USER_PROFILE, null)
        )
    }

    fun logout() {
        editor.clear()
        editor.apply()
    }
}
