package com.aurelioklv.githubuser.ui.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurelioklv.githubuser.data.FavoriteUserRepository
import com.aurelioklv.githubuser.data.local.FavoriteUser
import com.aurelioklv.githubuser.data.remote.api.ApiConfig
import com.aurelioklv.githubuser.data.remote.response.UserResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsViewModel(private val repository: FavoriteUserRepository) : ViewModel() {
    private val _user = MutableLiveData<UserResponse>()
    val user: LiveData<UserResponse> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun getUserDetails(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserDetails(username)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _errorMessage.value = ""
                    responseBody?.let {
                        _user.value = it
                    }
                    viewModelScope.launch {
                        _isFavorite.postValue(repository.isFavorite(_user.value!!.login))
                    }
                } else {
                    _errorMessage.value = response.message()
                    Log.e(TAG, "onResponse !isSuccessFul: $response")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


    fun setUserFavorite(favoriteUser: FavoriteUser) {
        viewModelScope.launch {
            val isFavorite = repository.isFavorite(favoriteUser.username)
            if (isFavorite) {
                repository.deleteFavoriteUser(favoriteUser)
                _isFavorite.postValue(false)
            } else {
                repository.insertFavoriteUser(favoriteUser)
                _isFavorite.postValue(true)
            }
        }
    }

    companion object {
        const val TAG = "DetailsViewModel"
    }
}