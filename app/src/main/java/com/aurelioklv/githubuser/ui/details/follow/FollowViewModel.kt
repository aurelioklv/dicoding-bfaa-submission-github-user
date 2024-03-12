package com.aurelioklv.githubuser.ui.details.follow

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aurelioklv.githubuser.R
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

    fun setData(username: String, type: Int, context: Context) {
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
                    responseBody?.let {
                        _followData.value = it
                    }
                } else {
                    Log.e(TAG, "onResponse !isSuccessful: $response")
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<FollowerFollowingItem>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                Toast.makeText(
                    context,
                    context.getString(R.string.error_message),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    companion object {
        const val TAG = "FollowViewModel"
    }
}