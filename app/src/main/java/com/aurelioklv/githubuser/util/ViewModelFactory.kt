package com.aurelioklv.githubuser.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aurelioklv.githubuser.data.FavoriteUserRepository
import com.aurelioklv.githubuser.data.SettingPreferences
import com.aurelioklv.githubuser.data.dataStore
import com.aurelioklv.githubuser.di.Injection
import com.aurelioklv.githubuser.ui.details.DetailsViewModel
import com.aurelioklv.githubuser.ui.favorite.FavoriteViewModel
import com.aurelioklv.githubuser.ui.main.MainViewModel

class ViewModelFactory private constructor(
    private val repository: FavoriteUserRepository,
    private val pref: SettingPreferences,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    SettingPreferences.getInstance(context.dataStore)
                )
            }
    }
}