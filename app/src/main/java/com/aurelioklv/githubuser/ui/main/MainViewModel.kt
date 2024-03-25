package com.aurelioklv.githubuser.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aurelioklv.githubuser.data.SettingPreferences
import com.aurelioklv.githubuser.data.remote.api.ApiConfig
import com.aurelioklv.githubuser.data.remote.response.UserSearchItem
import com.aurelioklv.githubuser.data.remote.response.UserSearchResponse
import kotlinx.coroutines.launch
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: SettingPreferences) : ViewModel() {
    private val _users = MutableLiveData<List<UserSearchItem>>()
    val users: LiveData<List<UserSearchItem>> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        searchUser(INITIAL_QUERY)
    }

    fun searchUser(query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchUsers(query)
        client.enqueue(object : Callback<UserSearchResponse> {
            override fun onResponse(
                call: retrofit2.Call<UserSearchResponse>,
                response: Response<UserSearchResponse>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _users.value = responseBody.items
                    }
                } else {
                    _errorMessage.value = response.message()
                    Log.e(TAG, "onResponse !isSuccessful: $response")
                }
            }

            override fun onFailure(call: retrofit2.Call<UserSearchResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    companion object {
        const val INITIAL_QUERY = "android"
        const val TAG = "MainViewModel"
    }
}