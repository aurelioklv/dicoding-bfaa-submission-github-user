package com.aurelioklv.githubuser.di

import android.content.Context
import com.aurelioklv.githubuser.data.FavoriteUserRepository
import com.aurelioklv.githubuser.data.local.FavoriteUserDatabase

object Injection {
    fun provideRepository(context: Context): FavoriteUserRepository {
        val database = FavoriteUserDatabase.getDatabase(context)
        val dao = database.favUserDao()
        return FavoriteUserRepository.getInstance(dao)
    }
}