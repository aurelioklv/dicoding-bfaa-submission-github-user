package com.aurelioklv.githubuser.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurelioklv.githubuser.data.FavoriteUserRepository
import com.aurelioklv.githubuser.data.local.FavoriteUser
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavoriteUserRepository) : ViewModel() {
    private val _favUsers = MutableLiveData<List<FavoriteUser>>()
    val favUsers: LiveData<List<FavoriteUser>> = _favUsers

    init {
        loadFavoriteUsers()
    }

    private fun loadFavoriteUsers() {
        viewModelScope.launch {
            repository.getFavoriteUser().observeForever {
                _favUsers.postValue(it)
            }
        }
    }
}