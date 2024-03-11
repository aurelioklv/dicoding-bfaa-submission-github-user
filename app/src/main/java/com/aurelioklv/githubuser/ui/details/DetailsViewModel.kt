package com.aurelioklv.githubuser.ui.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aurelioklv.githubuser.data.api.ApiConfig
import com.aurelioklv.githubuser.data.response.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsViewModel : ViewModel() {
    private val _user = MutableLiveData<UserResponse>()
    val user: LiveData<UserResponse> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUserDetails(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserDetails(username)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _user.value = responseBody
                    }
                } else {
                    Log.e(TAG, "onResponse !isSuccessFul: $response")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        const val TAG = "DetailsViewModel"
    }
}