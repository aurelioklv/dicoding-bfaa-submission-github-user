package com.aurelioklv.githubuser.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aurelioklv.githubuser.data.api.ApiConfig
import com.aurelioklv.githubuser.data.response.UserSearchItem
import com.aurelioklv.githubuser.data.response.UserSearchResponse
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _users = MutableLiveData<List<UserSearchItem>>()
    val users: LiveData<List<UserSearchItem>> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        searchUser(initialQuery)
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
                    Log.e(TAG, "onResponse !isSuccessful: $response")
                }
            }

            override fun onFailure(call: retrofit2.Call<UserSearchResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private val initialQuery = "android"
        const val TAG = "MainViewModel"
    }
}