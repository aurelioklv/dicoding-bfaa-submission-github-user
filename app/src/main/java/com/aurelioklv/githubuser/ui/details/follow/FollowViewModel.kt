package com.aurelioklv.githubuser.ui.details.follow

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aurelioklv.githubuser.data.api.ApiConfig
import com.aurelioklv.githubuser.data.response.FollowerFollowingItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowViewModel : ViewModel() {
    private val _followData = MutableLiveData<List<FollowerFollowingItem>>()
    val followData: LiveData<List<FollowerFollowingItem>> = _followData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun setData(username: String, type: Int) {
        _isLoading.value = true
        val client = when (type) {
            1 -> ApiConfig.getApiService().getFollowers(username)
            else -> ApiConfig.getApiService().getFollowing(username)
        }
        client.enqueue(object : Callback<List<FollowerFollowingItem>> {
            override fun onResponse(
                call: Call<List<FollowerFollowingItem>>,
                response: Response<List<FollowerFollowingItem>>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _errorMessage.value = ""
                    responseBody?.let {
                        _followData.value = it
                    }
                } else {
                    _errorMessage.value = response.message()
                    Log.e(TAG, "onResponse !isSuccessful: $response")
                }
            }

            override fun onFailure(call: Call<List<FollowerFollowingItem>>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        const val TAG = "FollowViewModel"
    }
}